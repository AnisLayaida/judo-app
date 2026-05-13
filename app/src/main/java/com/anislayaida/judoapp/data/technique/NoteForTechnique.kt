package com.anislayaida.judoapp.data.technique

data class NoteForTechnique(
    val id: String = "",
    val userId: String = "",
    val techniqueId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)