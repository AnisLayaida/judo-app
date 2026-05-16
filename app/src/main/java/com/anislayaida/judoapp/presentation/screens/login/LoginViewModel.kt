package com.anislayaida.judoapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.Response
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.data.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun signIn() {
        val email    = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your password") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepo.signInWithEmailAndPassword(email, password)

            when (result) {
                is Response.Success -> {
                    val uid = authRepo.getUserId() ?: ""

                    val role = try {
                        userRepo.getUserRole(uid)
                    } catch (e: Exception) {
                        UserRole.UNKNOWN
                    }

                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            navigateToHome = true,
                            userRole       = role
                        )
                    }
                }
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = when {
                                result.e.message?.contains("password") == true -> "Incorrect password"
                                result.e.message?.contains("user")     == true -> "No account found with this email"
                                result.e.message?.contains("network")  == true -> "No internet connection"
                                else -> "Sign in failed. Please try again"
                            }
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}