package com.anislayaida.judoapp.data

import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.data.user.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepo {
    val currentUser: FirebaseUser?
    suspend fun getCurrentUserRole(): UserRole
    suspend fun createUserProfile(newUserDetails: User): Response
    fun getUserId(): String?
    suspend fun signInWithEmailAndPassword(email: String, password: String): Response
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Response
    suspend fun sendEmailVerification(): Response
    suspend fun sendPasswordResetEmail(email: String): Response
    fun signOut()
}

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepo
) : AuthRepo {

    override val currentUser get() = auth.currentUser

    override suspend fun getCurrentUserRole(): UserRole {
        val uid = auth.currentUser?.uid ?: return UserRole.UNKNOWN
        return try {
            val user = userRepository.getUserById(uid)
            user?.role ?: UserRole.UNKNOWN
        } catch (e: Exception) {
            UserRole.UNKNOWN
        }
    }

    override suspend fun createUserProfile(newUserDetails: User): Response {
        return try {
            userRepository.createUserProfile(newUserDetails)
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun getUserId(): String? = auth.currentUser?.uid

    override suspend fun signInWithEmailAndPassword(
        email: String, password: String
    ): Response {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String, password: String
    ): Response {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Response {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Response {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun signOut() = auth.signOut()
}