package com.anislayaida.judoapp.data.club

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private var cachedClubs: List<String> = emptyList()
    private var hasFetched = false

    suspend fun searchClubs(query: String): List<String> {
        if (query.length < 2) return emptyList()
        if (!hasFetched) {
            cachedClubs = fetchFromFirestore()
            hasFetched = true
        }
        return cachedClubs
            .filter { it.contains(query, ignoreCase = true) }
            .take(6)
    }

    suspend fun getAllClubs(): List<String> {
        if (!hasFetched) {
            cachedClubs = fetchFromFirestore()
            hasFetched = true
        }
        return cachedClubs
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun fetchFromFirestore(): List<String> {
        return try {
            val doc = firestore
                .collection("clubs")
                .document("bjaSeedData")
                .get()
                .await()
            (doc.get("names") as? List<String>) ?: fallbackClubs
        } catch (e: Exception) {
            fallbackClubs
        }
    }

    private val fallbackClubs = listOf(
        "3K's Judo Club",
        "The Beacon Judokwai",
        "Birmingham Judo Club",
        "Bristol Judo Club",
        "Cardiff Judo Club",
        "Crystal Palace Judo Club",
        "Edinburgh Judo Club",
        "Leeds Judo Club",
        "Liverpool Judo Club",
        "London Judo Academy",
        "Manchester Judo Centre",
        "Newcastle Judo Club",
        "Nottingham Judo Club",
        "Sheffield Judo Club",
        "Steward Judo Academy"
    ).sorted()
}