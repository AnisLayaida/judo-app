package com.anislayaida.judoapp.unit.profile

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.presentation.screens.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var auth: FirebaseAuth
    private lateinit var userRepo: UserRepo
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        auth = mock()
        userRepo = mock()
        
        whenever(auth.currentUser).thenReturn(null)
        viewModel = ProfileViewModel(auth, userRepo)
    }

    @Test
    fun initial_user_is_null() {
        assert(viewModel.user.value == null)
    }

    @Test
    fun initial_is_loading_is_true() {
        
        assert(viewModel.isLoading.value)
    }

    @Test
    fun signOut_calls_auth_signOut() {
        viewModel.signOut()
        verify(auth).signOut()
    }
}