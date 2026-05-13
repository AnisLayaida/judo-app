package com.anislayaida.judoapp.presentation.screens.addTechnique

data class AddTechniqueUiState(
    val name: String = "",
    val nameJapanese: String = "",
    val category: String = "Nage-waza",
    val subcategory: String = "",
    val beltLevel: String = "White",
    val description: String = "",
    val isRestricted: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)