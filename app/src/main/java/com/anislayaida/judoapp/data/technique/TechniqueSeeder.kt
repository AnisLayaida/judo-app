package com.anislayaida.judoapp.data.technique

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TechniqueSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val techniqueCollection = firestore.collection("techniques")
    private val metaDoc = firestore.collection("meta").document("seeder")

    suspend fun seedIfNeeded() {
        val meta = metaDoc.get().await()
        if (meta.exists() && meta.getBoolean("seeded") == true) return

        val batch = firestore.batch()

        BjaSyllabus.techniques.forEach { technique ->
            val docRef = techniqueCollection.document()
            val techniqueWithId = technique.copy(uid = docRef.id)
            batch.set(docRef, techniqueWithId)
        }

        batch.commit().await()

        metaDoc.set(mapOf("seeded" to true)).await()
    }
}