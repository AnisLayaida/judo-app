package com.anislayaida.judoapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _userRole = MutableStateFlow(UserRole.UNKNOWN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            if (authRepo.currentUser == null) {
                _startDestination.value = NavScreen.LOGIN.route
            } else {
                val role = authRepo.getCurrentUserRole()
                _userRole.value = role
                _startDestination.value = when (role) {
                    UserRole.COACH -> NavScreen.COACH_HOME.route
                    UserRole.REFEREE -> NavScreen.REFEREE_HOME.route
                    else -> NavScreen.HOME.route
                }
            }
        }
    }

    fun signOut() {
        authRepo.signOut()
        _userRole.value = UserRole.UNKNOWN
        _startDestination.value = NavScreen.LOGIN.route
    }

    fun updateRole(role: UserRole) {
        _userRole.value = role
    }
}