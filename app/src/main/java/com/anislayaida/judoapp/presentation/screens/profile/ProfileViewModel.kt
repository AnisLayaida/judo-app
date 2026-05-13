package com.anislayaida.judoapp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRepo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepo
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadUser() }

    private fun loadUser() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _user.value = userRepo.getUserById(uid)
            _isLoading.value = false
        }
    }

    fun signOut() { auth.signOut() }
}