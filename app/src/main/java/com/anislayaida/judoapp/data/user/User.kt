package com.anislayaida.judoapp.data.user

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val beltGrade: String = "",
    val judoClub: String = "",
    val role: UserRole = UserRole.JUDOKA
)