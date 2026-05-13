package com.anislayaida.judoapp.data.technique

import com.anislayaida.judoapp.data.Repository
import com.anislayaida.judoapp.data.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TechniqueRepo : Repository<Technique> {
    fun getByCoach(coachUid: String): Flow<List<Technique>>
    suspend fun getById(id: String): Technique?
    fun getNotesForTechnique(userId: String, techniqueId: String): Flow<List<NoteForTechnique>>
    suspend fun addNote(userId: String, note: NoteForTechnique)
    suspend fun deleteNote(userId: String, techniqueId: String, noteId: String)
}

class TechniqueRepository @Inject constructor(
    private val dao: TechniqueDao
) : TechniqueRepo {

    override fun findAll(): Flow<List<Technique>> = dao.getAll()

    override fun getByCoach(coachUid: String): Flow<List<Technique>> = dao.getByCoach(coachUid)

    override suspend fun getById(id: String): Technique? = dao.getById(id)

    override suspend fun findById(id: String): Technique? = dao.getById(id)

    override suspend fun insert(item: Technique) = dao.add(item)

    override suspend fun update(item: Technique) = dao.update(item)

    override suspend fun delete(id: String) = dao.delete(id)

    override fun getNotesForTechnique(userId: String, techniqueId: String): Flow<List<NoteForTechnique>> =
        dao.getNotesForTechnique(userId, techniqueId)

    override suspend fun addNote(userId: String, note: NoteForTechnique) =
        dao.addNote(userId, note)

    override suspend fun deleteNote(userId: String, techniqueId: String, noteId: String) =
        dao.deleteNote(userId, techniqueId, noteId)
}