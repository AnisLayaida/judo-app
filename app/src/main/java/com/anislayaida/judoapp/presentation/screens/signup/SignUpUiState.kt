package com.anislayaida.judoapp.presentation.screens.signup

import com.anislayaida.judoapp.data.user.UserRole

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val dateOfBirth: String = "",
    val beltGrade: String = "",
    val judoClub: String = "",
    val role: UserRole = UserRole.JUDOKA,
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
    val errorMessage: String? = null
)