package com.anislayaida.judoapp.unit.auth

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.club.ClubRepository
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.presentation.screens.signup.SignUpViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepo: AuthRepo
    private lateinit var clubRepository: ClubRepository
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        authRepo = mock()
        clubRepository = mock()
        viewModel = SignUpViewModel(authRepo, clubRepository)
    }

    @Test
    fun initial_state_is_not_loading() {
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun initial_full_name_is_empty() {
        assert(viewModel.uiState.value.fullName.isEmpty())
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
    fun initial_belt_grade_is_empty() {
        assert(viewModel.uiState.value.beltGrade.isEmpty())
    }

    @Test
    fun initial_judo_club_is_empty() {
        assert(viewModel.uiState.value.judoClub.isEmpty())
    }

    @Test
    fun initial_error_message_is_null() {
        assert(viewModel.uiState.value.errorMessage == null)
    }

    @Test
    fun initial_is_signed_up_is_false() {
        assert(!viewModel.uiState.value.isSignedUp)
    }

    @Test
    fun onFullNameChange_updates_state() {
        viewModel.onFullNameChange("Anis Layaida")
        assert(viewModel.uiState.value.fullName == "Anis Layaida")
    }

    @Test
    fun onEmailChange_updates_state() {
        viewModel.onEmailChange("anis@judo.com")
        assert(viewModel.uiState.value.email == "anis@judo.com")
    }

    @Test
    fun onPasswordChange_updates_state() {
        viewModel.onPasswordChange("securePass123")
        assert(viewModel.uiState.value.password == "securePass123")
    }

    @Test
    fun onBeltGradeChange_updates_state() {
        viewModel.onBeltGradeChange("Blue")
        assert(viewModel.uiState.value.beltGrade == "Blue")
    }

    @Test
    fun onFullNameChange_reflects_multiple_updates() {
        viewModel.onFullNameChange("First Name")
        viewModel.onFullNameChange("Anis Layaida")
        assert(viewModel.uiState.value.fullName == "Anis Layaida")
    }

    @Test
    fun onEmailChange_with_empty_string_clears_email() {
        viewModel.onEmailChange("anis@judo.com")
        viewModel.onEmailChange("")
        assert(viewModel.uiState.value.email.isEmpty())
    }

    @Test
    fun onRoleChange_to_judoka_updates_state() {
        viewModel.onRoleChange(UserRole.JUDOKA)
        assert(viewModel.uiState.value.role == UserRole.JUDOKA)
    }

    @Test
    fun onRoleChange_to_coach_updates_state() {
        viewModel.onRoleChange(UserRole.COACH)
        assert(viewModel.uiState.value.role == UserRole.COACH)
    }

    @Test
    fun onRoleChange_to_referee_updates_state() {
        viewModel.onRoleChange(UserRole.REFEREE)
        assert(viewModel.uiState.value.role == UserRole.REFEREE)
    }

    @Test
    fun onRoleChange_can_switch_between_roles() {
        viewModel.onRoleChange(UserRole.COACH)
        viewModel.onRoleChange(UserRole.JUDOKA)
        assert(viewModel.uiState.value.role == UserRole.JUDOKA)
    }

    @Test
    fun onDateOfBirthChange_formats_two_digits() {
        viewModel.onDateOfBirthChange("01")
        assert(viewModel.uiState.value.dateOfBirth == "01")
    }

    @Test
    fun onDateOfBirthChange_formats_four_digits_with_slash() {
        viewModel.onDateOfBirthChange("0101")
        assert(viewModel.uiState.value.dateOfBirth == "01/01")
    }

    @Test
    fun onDateOfBirthChange_formats_full_date() {
        viewModel.onDateOfBirthChange("01012000")
        assert(viewModel.uiState.value.dateOfBirth == "01/01/2000")
    }

    @Test
    fun signUp_with_empty_name_sets_error_message() = runTest {
        viewModel.signUp()
        advanceUntilIdle()
        assert(viewModel.uiState.value.errorMessage != null)
    }

    @Test
    fun signUp_with_empty_name_does_not_set_loading() = runTest {
        viewModel.signUp()
        advanceUntilIdle()
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun signUp_with_only_name_sets_error_for_missing_email() = runTest {
        viewModel.onFullNameChange("Anis Layaida")
        viewModel.signUp()
        advanceUntilIdle()
        assert(viewModel.uiState.value.errorMessage != null)
    }

    @Test
    fun selectClub_updates_judo_club_in_state() {
        viewModel.selectClub("Staffordshire Judo Club")
        assert(viewModel.uiState.value.judoClub == "Staffordshire Judo Club")
    }

    @Test
    fun selectClub_clears_suggestions() {
        viewModel.selectClub("Staffordshire Judo Club")
        assert(viewModel.clubSuggestions.value.isEmpty())
    }
}