package com.anislayaida.judoapp.unit.grading

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.presentation.screens.grading.GradingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GradingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userRepo: UserRepo
    private lateinit var viewModel: GradingViewModel

    @Before
    fun setUp() {
        auth = mock()
        firestore = mock()
        userRepo = mock()
        
        whenever(auth.currentUser).thenReturn(null)
        viewModel = GradingViewModel(auth, firestore, userRepo)
    }

    @Test
    fun initial_current_belt_is_white() {
        assert(viewModel.currentBelt.value == "White")
    }

    @Test
    fun initial_judoka_name_is_empty() {
        assert(viewModel.judokaName.value.isEmpty())
    }

    @Test
    fun initial_is_under_16_is_false() {
        assert(!viewModel.isUnder16.value)
    }

    @Test
    fun initial_requests_is_empty() {
        assert(viewModel.requests.value.isEmpty())
    }

    @Test
    fun initial_is_loading_is_false() {
        assert(!viewModel.isLoading.value)
    }

    @Test
    fun initial_message_is_null() {
        assert(viewModel.message.value == null)
    }

    @Test
    fun clearMessage_sets_message_to_null() {
        viewModel.clearMessage()
        assert(viewModel.message.value == null)
    }

    @Test
    fun canRequest_returns_true_when_no_requests() {
        assert(viewModel.canRequest())
    }

    @Test
    fun cooldownMessage_returns_null_when_no_requests() {
        assert(viewModel.cooldownMessage() == null)
    }

    @Test
    fun nextGradeLabel_white_returns_red() {
        assert(viewModel.nextGradeLabel("White") == "Red")
    }

    @Test
    fun nextGradeLabel_red_returns_yellow() {
        assert(viewModel.nextGradeLabel("Red") == "Yellow")
    }

    @Test
    fun nextGradeLabel_yellow_returns_orange() {
        assert(viewModel.nextGradeLabel("Yellow") == "Orange")
    }

    @Test
    fun nextGradeLabel_orange_returns_green() {
        assert(viewModel.nextGradeLabel("Orange") == "Green")
    }

    @Test
    fun nextGradeLabel_green_returns_blue() {
        assert(viewModel.nextGradeLabel("Green") == "Blue")
    }

    @Test
    fun nextGradeLabel_blue_returns_brown() {
        assert(viewModel.nextGradeLabel("Blue") == "Brown")
    }

    @Test
    fun nextGradeLabel_brown_returns_black_1st_dan() {
        assert(viewModel.nextGradeLabel("Brown") == "Black - 1st Dan")
    }

    @Test
    fun nextGradeLabel_black_10th_dan_returns_null() {
        assert(viewModel.nextGradeLabel("Black - 10th Dan") == null)
    }

    @Test
    fun nextGradeLabel_unknown_belt_returns_null() {
        assert(viewModel.nextGradeLabel("Purple") == null)
    }

    @Test
    fun requestGrading_does_nothing_when_not_authenticated() {
        
        viewModel.requestGrading()
        assert(!viewModel.isLoading.value)
    }
}