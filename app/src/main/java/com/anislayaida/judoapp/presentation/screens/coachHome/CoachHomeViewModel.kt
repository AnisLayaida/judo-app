package com.anislayaida.judoapp.presentation.screens.coachHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.technique.GradingRequest
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private val REJECTION_REASONS = listOf(
    "Not enough training time at current grade",
    "Technique execution needs improvement",
    "Randori performance insufficient",
    "Japanese terminology knowledge lacking",
    "Combinations and counters not demonstrated clearly",
    "Not ready — coach's discretion"
)

@HiltViewModel
class CoachHomeViewModel @Inject constructor(
    private val techniqueRepo: TechniqueRepo,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepo: UserRepo
) : ViewModel() {

    private val _techniques = MutableStateFlow<List<Technique>>(emptyList())
    val techniques: StateFlow<List<Technique>> = _techniques

    private val _judokas = MutableStateFlow<List<User>>(emptyList())
    val judokas: StateFlow<List<User>> = _judokas

    private val _gradingRequests = MutableStateFlow<List<GradingRequest>>(emptyList())
    val gradingRequests: StateFlow<List<GradingRequest>> = _gradingRequests

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val rejectionReasons: List<String> = REJECTION_REASONS

    val techniqueCount: StateFlow<Int> = _techniques
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val memberCount: StateFlow<Int> = _judokas
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val pendingCount: StateFlow<Int> = _gradingRequests
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private var coachClub: String = ""

    init {
        loadTechniques()
        loadCoachThenJudokas()
        loadGradingRequests()
    }

    private fun loadTechniques() {
        viewModelScope.launch {
            techniqueRepo.findAll().collect { _techniques.value = it }
        }
    }

    private fun loadCoachThenJudokas() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val coach = userRepo.getUserById(uid)
            coachClub = coach?.judoClub ?: ""
            loadJudokas()
        }
    }

    private fun loadJudokas() {
        val query = if (coachClub.isNotEmpty()) {
            firestore.collection("users")
                .whereEqualTo("role", "JUDOKA")
                .whereEqualTo("judoClub", coachClub)
        } else {
            firestore.collection("users").whereEqualTo("role", "JUDOKA")
        }
        query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) _judokas.value = snapshot.toObjects<User>()
        }
    }

    private fun loadGradingRequests() {
        
        firestore.collection("gradingRequests")
            .whereIn("status", listOf("pending", "readiness_approved"))
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _gradingRequests.value = snapshot.toObjects<GradingRequest>()
                        .sortedByDescending { it.timestamp }
                }
            }
    }

    fun onSearchChanged(query: String) { _searchQuery.value = query }

    fun filteredTechniques(): List<Technique> {
        val q = _searchQuery.value.trim().lowercase()
        return if (q.isEmpty()) _techniques.value
        else _techniques.value.filter {
            it.name.lowercase().contains(q) ||
                    it.nameJapanese.contains(q) ||
                    it.category.lowercase().contains(q)
        }
    }

    
    fun approveReadiness(request: GradingRequest) {
        viewModelScope.launch {
            try {
                firestore.collection("gradingRequests")
                    .document(request.id)
                    .update(mapOf(
                        "status" to "readiness_approved",
                        "stage"  to "result"
                    ))
                    .await()
                _message.value = "${request.judokaName} is approved — record result after grading"
            } catch (e: Exception) {
                _message.value = "Failed to approve: ${e.message}"
            }
        }
    }

    
    fun rejectWithReason(request: GradingRequest, reason: String) {
        viewModelScope.launch {
            try {
                val cooldownUntil = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                firestore.collection("gradingRequests")
                    .document(request.id)
                    .update(mapOf(
                        "status"          to "rejected",
                        "rejectionReason" to reason,
                        "cooldownUntil"   to cooldownUntil
                    ))
                    .await()
                _message.value = "${request.judokaName}'s request rejected"
            } catch (e: Exception) {
                _message.value = "Failed to reject: ${e.message}"
            }
        }
    }

    
    fun recordPass(request: GradingRequest) {
        viewModelScope.launch {
            try {
                firestore.collection("gradingRequests")
                    .document(request.id)
                    .update("status", "passed")
                    .await()
                firestore.collection("users")
                    .document(request.judokaUid)
                    .update("beltGrade", request.requestedBelt)
                    .await()
                _message.value = "🎉 ${request.judokaName} passed — promoted to ${request.requestedBelt}!"
            } catch (e: Exception) {
                _message.value = "Failed to record pass: ${e.message}"
            }
        }
    }

    
    fun recordFail(request: GradingRequest) {
        viewModelScope.launch {
            try {
                val cooldownUntil = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                firestore.collection("gradingRequests")
                    .document(request.id)
                    .update(mapOf(
                        "status"        to "failed",
                        "cooldownUntil" to cooldownUntil
                    ))
                    .await()
                _message.value = "${request.judokaName} did not pass — 7 day cooldown applied"
            } catch (e: Exception) {
                _message.value = "Failed to record result: ${e.message}"
            }
        }
    }

    fun clearMessage() { _message.value = null }
}