package com.anislayaida.judoapp.unit.auth

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.presentation.screens.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepo: AuthRepo
    private lateinit var userRepo: UserRepo
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        authRepo = mock()
        userRepo = mock()
        viewModel = LoginViewModel(authRepo, userRepo)
    }

    @Test
    fun initial_state_is_not_loading() {
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun initial_email_is_empty() {
        assert(viewModel.uiState.value.email.isEmpty())
    }

    @Test
    fun initial_password_is_empty() {
        assert(viewModel.uiState.value.password.isEmpty())
    }

    @Test
    fun initial_error_message_is_null_or_empty() {
        val error = viewModel.uiState.value.errorMessage
        assert(error == null || error.isEmpty())
    }

    @Test
    fun onEmailChange_updates_email_in_state() {
        viewModel.onEmailChange("anis@judo.com")
        assert(viewModel.uiState.value.email == "anis@judo.com")
    }

    @Test
    fun onPasswordChange_updates_password_in_state() {
        viewModel.onPasswordChange("password123")
        assert(viewModel.uiState.value.password == "password123")
    }

    @Test
    fun onEmailChange_reflects_multiple_updates() {
        viewModel.onEmailChange("first@email.com")
        viewModel.onEmailChange("second@email.com")
        assert(viewModel.uiState.value.email == "second@email.com")
    }

    @Test
    fun onPasswordChange_reflects_multiple_updates() {
        viewModel.onPasswordChange("first")
        viewModel.onPasswordChange("second")
        assert(viewModel.uiState.value.password == "second")
    }

    @Test
    fun onEmailChange_with_empty_string_clears_email() {
        viewModel.onEmailChange("anis@judo.com")
        viewModel.onEmailChange("")
        assert(viewModel.uiState.value.email.isEmpty())
    }

    @Test
    fun onPasswordChange_with_empty_string_clears_password() {
        viewModel.onPasswordChange("password123")
        viewModel.onPasswordChange("")
        assert(viewModel.uiState.value.password.isEmpty())
    }

    @Test
    fun signIn_with_empty_email_does_not_set_loading() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.signIn()
        advanceUntilIdle()
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun signIn_with_empty_password_does_not_set_loading() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.signIn()
        advanceUntilIdle()
        assert(!viewModel.uiState.value.isLoading)
    }
}