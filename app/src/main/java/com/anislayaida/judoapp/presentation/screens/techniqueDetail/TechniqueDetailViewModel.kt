package com.anislayaida.judoapp.presentation.screens.techniqueDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.technique.NoteForTechnique
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TechniqueDetailViewModel @Inject constructor(
    private val techniqueRepo: TechniqueRepo,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _technique = MutableStateFlow<Technique?>(null)
    val technique: StateFlow<Technique?> = _technique

    private val _notes = MutableStateFlow<List<NoteForTechnique>>(emptyList())
    val notes: StateFlow<List<NoteForTechnique>> = _notes

    private val _newNoteText = MutableStateFlow("")
    val newNoteText: StateFlow<String> = _newNoteText

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun load(techniqueId: String) {
        viewModelScope.launch {
            _technique.value = techniqueRepo.getById(techniqueId)
            loadNotes(techniqueId)
        }
    }

    private fun loadNotes(techniqueId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            techniqueRepo.getNotesForTechnique(uid, techniqueId).collect {
                _notes.value = it
            }
        }
    }

    fun onNoteTextChanged(text: String) { _newNoteText.value = text }

    fun saveNote(techniqueId: String) {
        val uid  = auth.currentUser?.uid ?: return
        val text = _newNoteText.value.trim()
        if (text.isEmpty()) return
        _isSaving.value = true
        viewModelScope.launch {
            val note = NoteForTechnique(
                userId      = uid,
                techniqueId = techniqueId,
                content     = text
            )
            techniqueRepo.addNote(uid, note)
            _newNoteText.value = ""
            _isSaving.value = false
        }
    }

    fun deleteNote(techniqueId: String, noteId: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            techniqueRepo.deleteNote(uid, techniqueId, noteId)
        }
    }
}