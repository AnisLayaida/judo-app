package com.anislayaida.judoapp.unit.home

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var techniqueRepo: TechniqueRepo
    private lateinit var userRepo: UserRepo
    private lateinit var authRepo: AuthRepo
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        techniqueRepo = mock()
        userRepo = mock()
        authRepo = mock()
        whenever(techniqueRepo.findAll()).thenReturn(flowOf(emptyList()))
        viewModel = HomeViewModel(techniqueRepo, userRepo, authRepo)
    }

    @Test
    fun initial_selected_belt_is_null() {
        assert(viewModel.selectedBelt.value == null)
    }

    @Test
    fun initial_loading_state_is_true() {
        assert(viewModel.isLoading.value)
    }

    @Test
    fun initial_current_user_is_null() {
        assert(viewModel.currentUser.value == null)
    }

    @Test
    fun initial_filtered_techniques_is_empty() {
        assert(viewModel.filteredTechniques.value.isEmpty())
    }

    @Test
    fun initial_available_belts_is_empty() {
        assert(viewModel.availableBelts.value.isEmpty())
    }

    @Test
    fun onBeltSelected_updates_selected_belt() {
        viewModel.onBeltSelected("White")
        assert(viewModel.selectedBelt.value == "White")
    }

    @Test
    fun onBeltSelected_all_clears_belt_filter() {
        viewModel.onBeltSelected("Yellow")
        viewModel.onBeltSelected("__all__")
        assert(viewModel.selectedBelt.value == null)
    }

    @Test
    fun selecting_same_belt_twice_keeps_it_selected() {
        viewModel.onBeltSelected("White")
        viewModel.onBeltSelected("White")
        assert(viewModel.selectedBelt.value == "White")
    }

    @Test
    fun selecting_different_belt_updates_filter() {
        viewModel.onBeltSelected("White")
        viewModel.onBeltSelected("Blue")
        assert(viewModel.selectedBelt.value == "Blue")
    }

    @Test
    fun onBeltSelected_black_updates_correctly() {
        viewModel.onBeltSelected("Black")
        assert(viewModel.selectedBelt.value == "Black")
    }

    @Test
    fun onBeltSelected_orange_updates_correctly() {
        viewModel.onBeltSelected("Orange")
        assert(viewModel.selectedBelt.value == "Orange")
    }

    @Test
    fun belt_filter_can_be_set_and_then_cleared() {
        viewModel.onBeltSelected("Green")
        assert(viewModel.selectedBelt.value == "Green")
        viewModel.onBeltSelected("__all__")
        assert(viewModel.selectedBelt.value == null)
    }
}