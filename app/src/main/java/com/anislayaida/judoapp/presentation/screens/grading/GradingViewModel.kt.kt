package com.anislayaida.judoapp.presentation.screens.grading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.technique.GradingRequest
import com.anislayaida.judoapp.data.user.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GradingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userRepo: UserRepo
) : ViewModel() {

    private val _currentBelt = MutableStateFlow("White")
    val currentBelt: StateFlow<String> = _currentBelt

    private val _judokaName = MutableStateFlow("")
    val judokaName: StateFlow<String> = _judokaName

    private val _isUnder16 = MutableStateFlow(false)
    val isUnder16: StateFlow<Boolean> = _isUnder16

    private val _requests = MutableStateFlow<List<GradingRequest>>(emptyList())
    val requests: StateFlow<List<GradingRequest>> = _requests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init { loadProfile() }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val user = userRepo.getUserById(uid)
            _currentBelt.value = user?.beltGrade ?: "White"
            _judokaName.value  = user?.fullName ?: ""
            _isUnder16.value   = user?.dateOfBirth?.let { calculateAge(it) < 16 } ?: false
            loadRequests(uid)
        }
    }

    private fun loadRequests(uid: String) {
        firestore.collection("gradingRequests")
            .whereEqualTo("judokaUid", uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _requests.value = snapshot.toObjects(GradingRequest::class.java)
                        .sortedByDescending { it.timestamp }
                }
            }
    }

    fun requestGrading() {
        val uid     = auth.currentUser?.uid ?: return
        val current = _currentBelt.value
        val next    = nextBelt(current) ?: run {
            _message.value = "You have reached the highest grade"
            return
        }

        
        val lastRejected = _requests.value
            .filter { it.status == "rejected" || it.status == "failed" }
            .maxByOrNull { it.timestamp }

        if (lastRejected != null && System.currentTimeMillis() < lastRejected.cooldownUntil) {
            val daysLeft = ((lastRejected.cooldownUntil - System.currentTimeMillis()) / 86400000) + 1
            _message.value = "You must wait $daysLeft more day(s) before requesting again"
            return
        }

        val hasPending = _requests.value.any {
            it.status == "pending" || it.status == "readiness_approved"
        }
        if (hasPending) {
            _message.value = "You already have an active grading request"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val ref     = firestore.collection("gradingRequests").document()
                val request = GradingRequest(
                    id            = ref.id,
                    judokaUid     = uid,
                    judokaName    = _judokaName.value,
                    currentBelt   = current,
                    requestedBelt = next,
                    status        = "pending",
                    stage         = "readiness"
                )
                ref.set(request).await()
                _message.value = "Grading request submitted for $next"
            } catch (e: Exception) {
                _message.value = "Failed to submit request"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelGradingRequest(requestId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("gradingRequests")
                    .document(requestId)
                    .update("status", "cancelled")
                    .await()
                _message.value = "Grading request cancelled"
            } catch (e: Exception) {
                _message.value = "Failed to cancel request"
            }
        }
    }

    fun nextGradeLabel(current: String): String? = nextBelt(current)

    fun clearMessage() { _message.value = null }

    fun canRequest(): Boolean {
        val hasPending = _requests.value.any {
            it.status == "pending" || it.status == "readiness_approved"
        }
        if (hasPending) return false

        val lastRejected = _requests.value
            .filter { it.status == "rejected" || it.status == "failed" }
            .maxByOrNull { it.timestamp }

        return lastRejected == null || System.currentTimeMillis() >= lastRejected.cooldownUntil
    }

    fun cooldownMessage(): String? {
        val lastRejected = _requests.value
            .filter { it.status == "rejected" || it.status == "failed" }
            .maxByOrNull { it.timestamp } ?: return null

        if (System.currentTimeMillis() < lastRejected.cooldownUntil) {
            val daysLeft = ((lastRejected.cooldownUntil - System.currentTimeMillis()) / 86400000) + 1
            return "Cooldown: $daysLeft day(s) remaining"
        }
        return null
    }

    private fun calculateAge(dob: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birth     = LocalDate.parse(dob, formatter)
            Period.between(birth, LocalDate.now()).years
        } catch (e: Exception) { 99 }
    }

    private fun nextBelt(current: String): String? = when (current) {
        "White"            -> "Red"
        "Red"              -> "Yellow"
        "Yellow"           -> "Orange"
        "Orange"           -> "Green"
        "Green"            -> "Blue"
        "Blue"             -> "Brown"
        "Brown"            -> "Black - 1st Dan"
        "Black - 1st Dan"  -> "Black - 2nd Dan"
        "Black - 2nd Dan"  -> "Black - 3rd Dan"
        "Black - 3rd Dan"  -> "Black - 4th Dan"
        "Black - 4th Dan"  -> "Black - 5th Dan"
        "Black - 5th Dan"  -> "Black - 6th Dan"
        "Black - 6th Dan"  -> "Black - 7th Dan"
        "Black - 7th Dan"  -> "Black - 8th Dan"
        "Black - 8th Dan"  -> "Black - 9th Dan"
        "Black - 9th Dan"  -> "Black - 10th Dan"
        "Black - 10th Dan" -> null
        else               -> null
    }
}