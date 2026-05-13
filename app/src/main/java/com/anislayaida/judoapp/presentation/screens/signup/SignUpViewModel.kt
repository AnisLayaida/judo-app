package com.anislayaida.judoapp.presentation.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.Response
import com.anislayaida.judoapp.data.club.ClubRepository
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val clubRepo: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _clubSuggestions = MutableStateFlow<List<String>>(emptyList())
    val clubSuggestions: StateFlow<List<String>> = _clubSuggestions.asStateFlow()

    private var searchJob: Job? = null

    fun onFullNameChange(value: String)  = _uiState.update { it.copy(fullName  = value) }
    fun onEmailChange(value: String)     = _uiState.update { it.copy(email     = value) }
    fun onPasswordChange(value: String)  = _uiState.update { it.copy(password  = value) }
    fun onBeltGradeChange(value: String) = _uiState.update { it.copy(beltGrade = value) }
    fun onRoleChange(value: UserRole)    = _uiState.update { it.copy(role      = value) }

    fun onDateOfBirthChange(value: String) {
        val digits = value.filter { it.isDigit() }
        val formatted = when {
            digits.length <= 2 -> digits
            digits.length <= 4 -> "${digits.substring(0,2)}/${digits.substring(2)}"
            digits.length <= 8 -> "${digits.substring(0,2)}/${digits.substring(2,4)}/${digits.substring(4)}"
            else               -> "${digits.substring(0,2)}/${digits.substring(2,4)}/${digits.substring(4,8)}"
        }
        _uiState.update { it.copy(dateOfBirth = formatted) }
    }

    fun onJudoClubChange(value: String) {
        _uiState.update { it.copy(judoClub = value) }
        searchJob?.cancel()
        if (value.length < 2) { _clubSuggestions.value = emptyList(); return }
        searchJob = viewModelScope.launch {
            delay(500L)
            _clubSuggestions.value = clubRepo.searchClubs(value)
        }
    }

    fun selectClub(club: String) {
        searchJob?.cancel()
        _uiState.update { it.copy(judoClub = club) }
        _clubSuggestions.value = emptyList()
    }

    fun signUp() {
        val state = _uiState.value

        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name") }
            return
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a password") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.dateOfBirth.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your date of birth") }
            return
        }
        if (state.beltGrade.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select your belt grade") }
            return
        }
        if (state.judoClub.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your judo club") }
            return
        }

        // Coach must be 16 or over
        if (state.role == UserRole.COACH) {
            val age = try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val birth     = LocalDate.parse(state.dateOfBirth, formatter)
                Period.between(birth, LocalDate.now()).years
            } catch (e: Exception) { 99 }

            if (age < 16) {
                _uiState.update { it.copy(errorMessage = "You must be 16 or over to register as a Coach") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepo.signUpWithEmailAndPassword(
                state.email.trim(),
                state.password
            )

            when (result) {
                is Response.Success -> {
                    val uid  = authRepo.getUserId() ?: ""
                    val user = User(
                        uid         = uid,
                        fullName    = state.fullName.trim(),
                        email       = state.email.trim(),
                        dateOfBirth = state.dateOfBirth,
                        beltGrade   = state.beltGrade,
                        judoClub    = state.judoClub,
                        role        = state.role
                    )
                    authRepo.createUserProfile(user)
                    authRepo.sendEmailVerification()
                    _uiState.update { it.copy(isLoading = false, isSignedUp = true) }
                }
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = when {
                                result.e.message?.contains("email")    == true -> "This email is already registered"
                                result.e.message?.contains("password") == true -> "Password is too weak"
                                result.e.message?.contains("network")  == true -> "No internet connection"
                                else -> "Sign up failed, please try again"
                            }
                        )
                    }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}