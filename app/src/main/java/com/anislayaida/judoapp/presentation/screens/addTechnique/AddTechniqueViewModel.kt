package com.anislayaida.judoapp.presentation.screens.addTechnique

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTechniqueViewModel @Inject constructor(
    private val techniqueRepo: TechniqueRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTechniqueUiState())
    val uiState: StateFlow<AddTechniqueUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String)         { _uiState.update { it.copy(name = value) } }
    fun onJapaneseNameChange(value: String) { _uiState.update { it.copy(nameJapanese = value) } }
    fun onCategoryChange(value: String)     { _uiState.update { it.copy(category = value) } }
    fun onSubcategoryChange(value: String)  { _uiState.update { it.copy(subcategory = value) } }
    fun onBeltLevelChange(value: String)    { _uiState.update { it.copy(beltLevel = value) } }
    fun onDescriptionChange(value: String)  { _uiState.update { it.copy(description = value) } }
    fun onRestrictedChange(value: Boolean)  { _uiState.update { it.copy(isRestricted = value) } }

    fun saveTechnique() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Technique name is required") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val technique = Technique(
                    uid          = UUID.randomUUID().toString(),
                    name         = state.name.trim(),
                    nameJapanese = state.nameJapanese.trim(),
                    category     = state.category,
                    subcategory  = state.subcategory.trim(),
                    beltLevel    = state.beltLevel,
                    beltOrder    = beltOrder(state.beltLevel),
                    description  = state.description.trim(),
                    isRestricted = state.isRestricted,
                    sortOrder    = 999
                )
                techniqueRepo.insert(technique)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to save technique") }
            }
        }
    }

    private fun beltOrder(belt: String): Int = when (belt) {
        "White"  -> 0; "Yellow" -> 1; "Orange" -> 2; "Red" -> 3
        "Green"  -> 4; "Blue"   -> 5; "Brown"  -> 6; "Black" -> 7
        else     -> 8
    }
}