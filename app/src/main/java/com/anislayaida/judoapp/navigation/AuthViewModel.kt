package com.anislayaida.judoapp.navigation

import androidx.lifecycle.ViewModel
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _userRole = MutableStateFlow(UserRole.UNKNOWN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    fun updateRole(role: UserRole) {
        _userRole.value = role
    }

    fun signOut() = authRepo.signOut()
}