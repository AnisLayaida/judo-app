package com.anislayaida.judoapp.data.user

import com.anislayaida.judoapp.data.Repository
import com.anislayaida.judoapp.data.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepo : Repository<User> {
    suspend fun createUserProfile(newUserDetails: User): Response
    suspend fun getUserRole(uid: String): UserRole
    suspend fun getUserById(uid: String): User?
}

class UserRepository @Inject constructor(
    private val dao: UserDao
) : UserRepo {

    override suspend fun createUserProfile(newUserDetails: User): Response =
        dao.create(newUserDetails)

    override suspend fun insert(item: User) = dao.update(user = item)

    override suspend fun delete(id: String) = dao.delete(userId = id)

    override suspend fun update(item: User) = dao.update(user = item)

    override fun findAll(): Flow<List<User>> = dao.getAll()

    override suspend fun findById(id: String): User? = dao.getById(id)

    override suspend fun getUserById(uid: String): User? = dao.getById(uid)

    override suspend fun getUserRole(uid: String): UserRole {
        val user = dao.getById(uid)
        return user?.role ?: UserRole.UNKNOWN
    }
}