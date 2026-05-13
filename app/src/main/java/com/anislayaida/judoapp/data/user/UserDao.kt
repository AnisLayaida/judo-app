package com.anislayaida.judoapp.data.user

import com.anislayaida.judoapp.data.Response
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDao @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")

    suspend fun create(user: User): Response {
        return try {
            update(user)
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun update(user: User) {
        userCollection.document(user.uid).set(user).await()
    }

    suspend fun delete(userId: String) {
        userCollection.document(userId).delete().await()
    }

    fun getAll(): Flow<List<User>> {
        return userCollection
            .orderBy("fullName")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(User::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun getById(id: String): User? {
        val snapshot = userCollection.document(id).get().await()
        return if (snapshot.exists()) snapshot.toObject(User::class.java) else null
    }
}