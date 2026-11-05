package com.example.data.model

enum class CompanionCharacter(val displayName: String, val appTitle: String, val defaultVoicePitch: Float) {
    TABASSUM("Tabassum", "Tabu AI", 1.25f),
    ARFAN("Arfan", "Arf AI", 0.90f)
}

enum class RelationshipMode(val label: String, val description: String) {
    GIRLFRIEND("Girlfriend Mode", "Sweet, loving, affectionate anime partner"),
    BOYFRIEND("Boyfriend Mode", "Caring, protective, and charming companion"),
    BEST_FRIEND("Best Friend Mode", "Warm, funny, supportive, and cheerful buddy"),
    JARVIS("JARVIS Assistant", "Witty, efficient, smart executive phone assistant")
}

enum class CompanionEmotion(val emoji: String, val label: String) {
    HAPPY("😊", "Happy"),
    LOVING("💖", "In Love"),
    SHY("😳", "Blushing"),
    THINKING("🤔", "Thinking"),
    SLEEPY("😴", "Sleepy"),
    EXCITED("✨", "Excited"),
    TALKING("🗣️", "Speaking")
}

data class UserProfile(
    val name: String = "",
    val nickname: String = "",
    val favoriteThings: String = "",
    val wakeWordEnabled: Boolean = true
)
