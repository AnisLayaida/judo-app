package com.anislayaida.judoapp.presentation.screens.login

import com.anislayaida.judoapp.data.user.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToHome: Boolean = false,
    val userRole: UserRole = UserRole.UNKNOWN
)