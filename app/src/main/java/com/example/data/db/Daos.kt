package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.JournalEntry
import com.example.data.model.MemoryFact
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntry(id: Long)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_facts ORDER BY timestamp DESC")
    fun getAllFacts(): Flow<List<MemoryFact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: MemoryFact): Long

    @Query("SELECT * FROM memory_facts WHERE `key` = :key LIMIT 1")
    suspend fun getFactByKey(key: String): MemoryFact?

    @Query("DELETE FROM memory_facts WHERE id = :id")
    suspend fun deleteFact(id: Long)
}
