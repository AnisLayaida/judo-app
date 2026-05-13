package com.anislayaida.judoapp.data.technique

import com.google.firebase.firestore.DocumentId

data class Technique(
    @DocumentId val uid: String = "",
    val name: String = "",
    val nameJapanese: String = "",
    val category: String = "",
    val subcategory: String = "",
    val beltLevel: String = "",
    val beltOrder: Int = 0,
    val description: String = "",
    val isRestricted: Boolean = false,
    val sortOrder: Int = 0,
    val videoUrl: String = ""
)