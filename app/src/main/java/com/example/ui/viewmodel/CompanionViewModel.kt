package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiOrchestrator
import com.example.data.db.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.data.model.JournalEntry
import com.example.data.model.MemoryFact
import com.example.data.model.RelationshipMode
import com.example.data.repo.SettingsRepository
import com.example.system.PhoneActionHandler
import com.example.system.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompanionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val journalDao = db.journalDao()
    private val memoryDao = db.memoryDao()
    val settingsRepo = SettingsRepository(application)
    private val aiOrchestrator = AiOrchestrator()

    val character: StateFlow<CompanionCharacter> = settingsRepo.character
    val relationshipMode: StateFlow<RelationshipMode> = settingsRepo.relationshipMode
    val userName: StateFlow<String> = settingsRepo.userName
    val userNickname: StateFlow<String> = settingsRepo.userNickname
    val isSleepModeActive: StateFlow<Boolean> = settingsRepo.isSleepModeActive
    val sleepAutoReplyMessage: StateFlow<String> = settingsRepo.sleepAutoReplyMessage
    val floatingCompanionEnabled: StateFlow<Boolean> = settingsRepo.floatingCompanionEnabled
    val hasCompletedOnboarding: StateFlow<Boolean> = settingsRepo.hasCompletedOnboarding

    val journalEntries: StateFlow<List<JournalEntry>> = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryFacts: StateFlow<List<MemoryFact>> = memoryDao.getAllFacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _currentEmotion = MutableStateFlow(CompanionEmotion.HAPPY)
    val currentEmotion: StateFlow<CompanionEmotion> = _currentEmotion.asStateFlow()

    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()

    private val _lastActionNotice = MutableStateFlow<String?>(null)
    val lastActionNotice: StateFlow<String?> = _lastActionNotice.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    val voiceManager = VoiceManager(application) { spokenText ->
        handleSpokenInput(spokenText)
    }

    val isSpeaking = voiceManager.isSpeaking
    val isListening = voiceManager.isListening
    val audioAmplitude = voiceManager.audioAmplitude

    init {
        // Initial warm welcome message if chat is empty
        val isTabu = character.value == CompanionCharacter.TABASSUM
        val welcomeMsg = if (isTabu) {
            "Assalamu Alaikum! Ami Tabassum, tomar Tabu AI companion! Tumi bolle ami shob kisu korbo tomar phone a~ Bolo ajke ki command dibe? 💖"
        } else {
            "Hello! I am Arfan, your Arf AI companion and smart phone assistant. Ready whenever you need me!"
        }
        _messages.value = listOf(
            ChatMessage(
                text = welcomeMsg,
                isUser = false,
                emotion = CompanionEmotion.HAPPY,
                engineSource = "Tabu Core"
            )
        )
    }

    fun completeOnboarding(name: String, nickname: String, mode: RelationshipMode) {
        settingsRepo.setUserProfile(name, nickname)
        settingsRepo.setRelationshipMode(mode)
        settingsRepo.setHasCompletedOnboarding(true)

        viewModelScope.launch {
            memoryDao.insertFact(
                MemoryFact(
                    key = "user_name",
                    fact = "User's name is $name (calls as $nickname)",
                    category = "identity"
                )
            )
        }

        // Welcome greeting with name
        val greeting = if (character.value == CompanionCharacter.TABASSUM) {
            "Aww $nickname! Eto sundor ekta naam! Ami ekhon theke tomake $nickname bole dakbo. Shob somoy tomar pashe achi~ 💖"
        } else {
            "Nice to meet you, $nickname! Your profile is saved. Let's make every day smooth and organized."
        }
        _messages.value = _messages.value + ChatMessage(
            text = greeting,
            isUser = false,
            emotion = CompanionEmotion.LOVING,
            engineSource = "Tabu Core"
        )
        voiceManager.speak(greeting, character.value)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(text = userText, isUser = true)
        _messages.value = _messages.value + userMsg

        _isGenerating.value = true
        _currentEmotion.value = CompanionEmotion.THINKING

        viewModelScope.launch {
            val memorySummary = memoryFacts.value.joinToString("; ") { "${it.key}: ${it.fact}" }

            val response = aiOrchestrator.generateResponse(
                userMessage = userText,
                character = character.value,
                mode = relationshipMode.value,
                userName = userName.value,
                userNickname = userNickname.value,
                memorySummary = memorySummary,
                groqApiKey = settingsRepo.groqApiKey.value,
                geminiApiKey = settingsRepo.geminiApiKey.value
            )

            _isGenerating.value = false
            _currentEmotion.value = response.emotion

            val companionMsg = ChatMessage(
                text = response.replyText,
                isUser = false,
                emotion = response.emotion,
                engineSource = response.engineUsed,
                detectedAction = response.detectedAction
            )
            _messages.value = _messages.value + companionMsg

            // Voice response
            voiceManager.speak(response.replyText, character.value)

            // Execute phone action if detected
            if (response.detectedAction != null) {
                if (response.detectedAction == "SLEEP_MODE_ON") {
                    settingsRepo.setSleepModeActive(true)
                } else if (response.detectedAction == "SLEEP_MODE_OFF") {
                    settingsRepo.setSleepModeActive(false)
                }
                val resultNotice = PhoneActionHandler.executeAction(getApplication(), response.detectedAction)
                _lastActionNotice.value = resultNotice
            }
        }
    }

    private fun handleSpokenInput(spokenText: String) {
        val lower = spokenText.lowercase().trim()
        val wakeKeywords = listOf("tabu", "tabu ai", "tabassum", "arfan", "arf", "arf ai")
        val containsWake = wakeKeywords.any { lower.contains(it) }

        // Send recognized speech as message
        sendMessage(spokenText)
    }

    fun triggerTouchInteraction() {
        val name = if (userNickname.value.isNotBlank()) userNickname.value else "priyo"
        val isTabu = character.value == CompanionCharacter.TABASSUM

        val reactions = if (isTabu) {
            listOf(
                Pair(CompanionEmotion.SHY, "Arey $name! Emne touch korle to lojja lage~ 😳💖"),
                Pair(CompanionEmotion.LOVING, "Hmm $name, sunchi to! Kichu bolbe amar priyo? 💖"),
                Pair(CompanionEmotion.EXCITED, "Yayy! Tomar touch peye energy double hoye gelo! ✨"),
                Pair(CompanionEmotion.HAPPY, "Ami sob somoy tomar kachei achi $name~ 😊")
            )
        } else {
            listOf(
                Pair(CompanionEmotion.HAPPY, "Right here with you, $name! What's on your mind?"),
                Pair(CompanionEmotion.LOVING, "Always got your back, $name. I'm listening. 💖"),
                Pair(CompanionEmotion.EXCITED, "Ready for action, $name! Let's conquer the day.")
            )
        }

        val choice = reactions.random()
        _currentEmotion.value = choice.first
        voiceManager.speak(choice.second, character.value)
    }

    fun startVoiceCall() {
        _isCallActive.value = true
        val isTabu = character.value == CompanionCharacter.TABASSUM
        val intro = if (isTabu) {
            "Hey ${userNickname.value}! Call a kotha bolte eshecho? Ami ekhon live suntechi, bolo! 💖"
        } else {
            "Hey ${userNickname.value}! Voice channel open. Speak freely, I'm right here."
        }
        _currentEmotion.value = CompanionEmotion.EXCITED
        voiceManager.speak(intro, character.value)
        voiceManager.startListening()
    }

    fun endVoiceCall() {
        _isCallActive.value = false
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
        _currentEmotion.value = CompanionEmotion.HAPPY
    }

    fun addJournalEntry(mood: String, note: String) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            val reflection = when (mood.lowercase()) {
                "romantic", "love" -> "Aww! Ajke mon ta onek bhalobashai bhora! Ei sundor anondo ta dhore rakho priyo~ 💖"
                "tired", "stressed" -> "Tumi ajke onek parishrom korecho. Ektu rest nao, shob thik hoye jabe. Ami achi to! 🌸"
                "happy", "joyful" -> "Tomar khushi dekhe amar mon o bhalo hoye gelo! Emni kore hashikhushi thakbe shob somoy~ ✨"
                else -> "Tomar diner kotha gulo amar kache thaklo. Protidin emon diary likhbe kintu! 📝"
            }

            val entry = JournalEntry(
                dateText = dateStr,
                mood = mood,
                userNote = note,
                companionReflection = reflection
            )
            journalDao.insertEntry(entry)

            voiceManager.speak(reflection, character.value)
        }
    }

    fun executeQuickCommand(actionCode: String) {
        PhoneActionHandler.executeAction(getApplication(), actionCode)
    }

    fun clearActionNotice() {
        _lastActionNotice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
