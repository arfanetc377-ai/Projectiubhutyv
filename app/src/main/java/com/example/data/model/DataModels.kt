package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateText: String,
    val mood: String,
    val userNote: String,
    val companionReflection: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_facts")
data class MemoryFact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val fact: String,
    val category: String = "general",
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val emotion: CompanionEmotion = CompanionEmotion.HAPPY,
    val engineSource: String = "Tabu Core", // "Groq Llama 3", "Gemini AI", "Tabu Core"
    val detectedAction: String? = null // e.g. "OPEN_WHATSAPP", "SLEEP_MODE"
)
