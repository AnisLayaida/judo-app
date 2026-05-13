package com.anislayaida.judoapp.data.technique

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TechniqueDao @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val techniqueCollection = firestore.collection("techniques")

    suspend fun add(technique: Technique) {
        val newDocRef = techniqueCollection.document()
        val techniqueWithId = technique.copy(uid = newDocRef.id)
        newDocRef.set(techniqueWithId).await()
    }

    suspend fun update(technique: Technique) {
        techniqueCollection.document(technique.uid).set(technique).await()
    }

    suspend fun delete(techniqueId: String) {
        techniqueCollection.document(techniqueId).delete().await()
    }

    fun getAll(): Flow<List<Technique>> {
        return techniqueCollection
            .orderBy("name")
            .snapshots()
            .map { snapshot -> snapshot.toObjects<Technique>() }
            .catch { emit(emptyList()) }
    }

    fun getByCoach(coachUid: String): Flow<List<Technique>> {
        return techniqueCollection
            .whereEqualTo("addedByUid", coachUid)
            .orderBy("name")
            .snapshots()
            .map { snapshot -> snapshot.toObjects<Technique>() }
            .catch { emit(emptyList()) }
    }

    suspend fun getById(id: String): Technique? {
        val snapshot = techniqueCollection.document(id).get().await()
        return snapshot.toObject(Technique::class.java)
    }

    fun getNotesForTechnique(userId: String, techniqueId: String): Flow<List<NoteForTechnique>> {
        return firestore
            .collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("techniqueId", techniqueId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects<NoteForTechnique>() }
            .catch { emit(emptyList()) }
    }

    suspend fun addNote(userId: String, note: NoteForTechnique) {
        val ref = firestore
            .collection("users")
            .document(userId)
            .collection("notes")
            .document()
        val noteWithId = note.copy(id = ref.id)
        ref.set(noteWithId).await()
    }

    suspend fun deleteNote(userId: String, techniqueId: String, noteId: String) {
        firestore
            .collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId)
            .delete()
            .await()
    }
}