package com.example.reminderapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<Message>>

    @Query("SELECT * FROM messages")
    suspend fun getAllSync(): List<Message>

    @Query("SELECT * FROM messages ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomMessage(): Message?

    @Insert
    suspend fun insert(message: Message): Long

    @Update
    suspend fun update(message: Message)

    @Delete
    suspend fun delete(message: Message)
}
