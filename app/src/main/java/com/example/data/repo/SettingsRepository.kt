package com.example.data.repo

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CompanionCharacter
import com.example.data.model.RelationshipMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tabu_settings_prefs", Context.MODE_PRIVATE)

    private val _character = MutableStateFlow(loadCharacter())
    val character: StateFlow<CompanionCharacter> = _character.asStateFlow()

    private val _relationshipMode = MutableStateFlow(loadRelationshipMode())
    val relationshipMode: StateFlow<RelationshipMode> = _relationshipMode.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userNickname = MutableStateFlow(prefs.getString("user_nickname", "") ?: "")
    val userNickname: StateFlow<String> = _userNickname.asStateFlow()

    private val _groqApiKey = MutableStateFlow(prefs.getString("groq_api_key", "") ?: "")
    val groqApiKey: StateFlow<String> = _groqApiKey.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _isSleepModeActive = MutableStateFlow(prefs.getBoolean("sleep_mode_active", false))
    val isSleepModeActive: StateFlow<Boolean> = _isSleepModeActive.asStateFlow()

    private val _sleepAutoReplyMessage = MutableStateFlow(
        prefs.getString("sleep_auto_reply", "Shh! User is sleeping right now. Tabu AI will notify him as soon as he wakes up! 🌙")
            ?: "Shh! User is sleeping right now. Tabu AI will notify him as soon as he wakes up! 🌙"
    )
    val sleepAutoReplyMessage: StateFlow<String> = _sleepAutoReplyMessage.asStateFlow()

    private val _floatingCompanionEnabled = MutableStateFlow(prefs.getBoolean("floating_companion_enabled", true))
    val floatingCompanionEnabled: StateFlow<Boolean> = _floatingCompanionEnabled.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.getBoolean("has_completed_onboarding", false))
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private fun loadCharacter(): CompanionCharacter {
        val name = prefs.getString("active_character", CompanionCharacter.TABASSUM.name)
        return try {
            CompanionCharacter.valueOf(name ?: CompanionCharacter.TABASSUM.name)
        } catch (_: Exception) {
            CompanionCharacter.TABASSUM
        }
    }

    private fun loadRelationshipMode(): RelationshipMode {
        val mode = prefs.getString("relationship_mode", RelationshipMode.GIRLFRIEND.name)
        return try {
            RelationshipMode.valueOf(mode ?: RelationshipMode.GIRLFRIEND.name)
        } catch (_: Exception) {
            RelationshipMode.GIRLFRIEND
        }
    }

    fun setCharacter(character: CompanionCharacter) {
        prefs.edit().putString("active_character", character.name).apply()
        _character.value = character
    }

    fun setRelationshipMode(mode: RelationshipMode) {
        prefs.edit().putString("relationship_mode", mode.name).apply()
        _relationshipMode.value = mode
    }

    fun setUserProfile(name: String, nickname: String) {
        prefs.edit().putString("user_name", name).putString("user_nickname", nickname).apply()
        _userName.value = name
        _userNickname.value = nickname
    }

    fun setGroqApiKey(key: String) {
        prefs.edit().putString("groq_api_key", key.trim()).apply()
        _groqApiKey.value = key.trim()
    }

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
        _geminiApiKey.value = key.trim()
    }

    fun setSleepModeActive(active: Boolean) {
        prefs.edit().putBoolean("sleep_mode_active", active).apply()
        _isSleepModeActive.value = active
    }

    fun setSleepAutoReplyMessage(msg: String) {
        prefs.edit().putString("sleep_auto_reply", msg).apply()
        _sleepAutoReplyMessage.value = msg
    }

    fun setFloatingCompanionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("floating_companion_enabled", enabled).apply()
        _floatingCompanionEnabled.value = enabled
    }

    fun setHasCompletedOnboarding(completed: Boolean) {
        prefs.edit().putBoolean("has_completed_onboarding", completed).apply()
        _hasCompletedOnboarding.value = completed
    }
}
