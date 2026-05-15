package com.anislayaida.judoapp.unit.techniqueDetail

import com.anislayaida.judoapp.MainDispatcherRule
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.presentation.screens.techniqueDetail.TechniqueDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TechniqueDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var techniqueRepo: TechniqueRepo
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: TechniqueDetailViewModel

    @Before
    fun setUp() {
        techniqueRepo = mock()
        auth = mock()
        
        whenever(auth.currentUser).thenReturn(null)
        viewModel = TechniqueDetailViewModel(techniqueRepo, auth)
    }

    @Test
    fun initial_technique_is_null() {
        assert(viewModel.technique.value == null)
    }

    @Test
    fun initial_notes_is_empty() {
        assert(viewModel.notes.value.isEmpty())
    }

    @Test
    fun initial_new_note_text_is_empty() {
        assert(viewModel.newNoteText.value.isEmpty())
    }

    @Test
    fun initial_is_saving_is_false() {
        assert(!viewModel.isSaving.value)
    }

    @Test
    fun onNoteTextChanged_updates_note_text() {
        viewModel.onNoteTextChanged("Great technique for gripping")
        assert(viewModel.newNoteText.value == "Great technique for gripping")
    }

    @Test
    fun onNoteTextChanged_reflects_multiple_updates() {
        viewModel.onNoteTextChanged("First note")
        viewModel.onNoteTextChanged("Updated note")
        assert(viewModel.newNoteText.value == "Updated note")
    }

    @Test
    fun onNoteTextChanged_with_empty_string_clears_text() {
        viewModel.onNoteTextChanged("Some note")
        viewModel.onNoteTextChanged("")
        assert(viewModel.newNoteText.value.isEmpty())
    }

    @Test
    fun saveNote_with_empty_text_does_not_set_saving() {
        viewModel.onNoteTextChanged("")
        viewModel.saveNote("technique-1")
        assert(!viewModel.isSaving.value)
    }

    @Test
    fun saveNote_with_whitespace_only_does_not_set_saving() {
        
        viewModel.onNoteTextChanged("   ")
        viewModel.saveNote("technique-1")
        assert(!viewModel.isSaving.value)
    }

    @Test
    fun saveNote_when_not_authenticated_does_not_set_saving() {
        viewModel.onNoteTextChanged("Valid note text")
        viewModel.saveNote("technique-1")
        assert(!viewModel.isSaving.value)
    }

    @Test
    fun deleteNote_when_not_authenticated_does_not_crash() {
        viewModel.deleteNote("technique-1", "note-1")
        assert(viewModel.notes.value.isEmpty())
    }
}