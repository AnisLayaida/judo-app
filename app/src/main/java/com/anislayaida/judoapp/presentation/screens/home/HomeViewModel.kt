package com.anislayaida.judoapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val techniqueRepo: TechniqueRepo,
    private val userRepo: UserRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _selectedBelt = MutableStateFlow<String?>(null)
    val selectedBelt: StateFlow<String?> = _selectedBelt.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = authRepo.getUserId() ?: return@launch
            _currentUser.value = userRepo.findById(uid)
        }
    }

    private val allTechniques: StateFlow<List<Technique>> =
        techniqueRepo.findAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredTechniques: StateFlow<List<Technique>> = combine(
        allTechniques, _selectedBelt, _currentUser
    ) { techniques, belt, user ->
        _isLoading.value = false
        val isUnder16 = user?.dateOfBirth?.let { calculateAge(it) < 16 } ?: false
        techniques
            .filter { if (isUnder16) !it.isRestricted else true }
            .filter { if (belt != null) it.beltLevel == belt else true }
            .sortedWith(compareBy({ it.beltOrder }, { it.sortOrder }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableBelts: StateFlow<List<String>> = allTechniques
        .map { techniques ->
            techniques.map { it.beltLevel }
                .distinct()
                .sortedBy { beltSortOrder(it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onBeltSelected(belt: String) {
        if (belt == "__all__") {
            _selectedBelt.value = null  // All chip always resets to show everything
        } else if (_selectedBelt.value != belt) {
            _selectedBelt.value = belt  // Only switch if it's a different belt
        }
        // Tapping the already-selected belt does nothing
    }

    private fun calculateAge(dob: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birth = LocalDate.parse(dob, formatter)
            Period.between(birth, LocalDate.now()).years
        } catch (e: Exception) { 99 }
    }

    private fun beltSortOrder(belt: String): Int = when (belt) {
        "White"  -> 0
        "Yellow" -> 1
        "Orange" -> 2
        "Red"    -> 3
        "Green"  -> 4
        "Blue"   -> 5
        "Brown"  -> 6
        "Black"  -> 7
        else     -> 8
    }
}