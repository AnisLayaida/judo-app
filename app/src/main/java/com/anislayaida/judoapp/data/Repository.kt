package com.anislayaida.judoapp.data

import kotlinx.coroutines.flow.Flow

interface Repository<T> {
    suspend fun delete(id: String)
    suspend fun insert(item: T)
    suspend fun update(item: T)
    fun findAll(): Flow<List<T>>
    suspend fun findById(id: String): T?
}