package com.example.ai

import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.data.model.RelationshipMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiOrchestrator {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    data class AiResponse(
        val replyText: String,
        val emotion: CompanionEmotion,
        val detectedAction: String?,
        val engineUsed: String
    )

    suspend fun generateResponse(
        userMessage: String,
        character: CompanionCharacter,
        mode: RelationshipMode,
        userName: String,
        userNickname: String,
        memorySummary: String,
        groqApiKey: String,
        geminiApiKey: String
    ): AiResponse = withContext(Dispatchers.IO) {
        val systemPrompt = PersonaPrompts.buildSystemPrompt(
            character = character,
            mode = mode,
            userName = userName,
            userNickname = userNickname,
            memorySummary = memorySummary
        )

        // 1. Try Groq API first if key exists (fastest response)
        if (groqApiKey.isNotBlank()) {
            try {
                val groqResult = callGroqApi(userMessage, systemPrompt, groqApiKey)
                if (groqResult != null) {
                    val (emotion, action, text) = PersonaPrompts.parseEmotionAndAction(groqResult)
                    return@withContext AiResponse(
                        replyText = text,
                        emotion = emotion,
                        detectedAction = action,
                        engineUsed = "Groq Llama 3"
                    )
                }
            } catch (_: Exception) {
                // Fall through to Gemini or offline engine
            }
        }

        // 2. Try Gemini API if key exists
        if (geminiApiKey.isNotBlank()) {
            try {
                val geminiResult = callGeminiApi(userMessage, systemPrompt, geminiApiKey)
                if (geminiResult != null) {
                    val (emotion, action, text) = PersonaPrompts.parseEmotionAndAction(geminiResult)
                    return@withContext AiResponse(
                        replyText = text,
                        emotion = emotion,
                        detectedAction = action,
                        engineUsed = "Gemini AI"
                    )
                }
            } catch (_: Exception) {
                // Fall through to offline companion
            }
        }

        // 3. Fallback Companion Logic (Always works offline & without keys)
        val offlineResult = generateOfflineCompanionResponse(
            query = userMessage,
            character = character,
            mode = mode,
            userName = userName,
            userNickname = userNickname
        )
        val (emotion, action, text) = PersonaPrompts.parseEmotionAndAction(offlineResult)
        return@withContext AiResponse(
            replyText = text,
            emotion = emotion,
            detectedAction = action,
            engineUsed = "Tabu Core"
        )
    }

    private fun callGroqApi(userMessage: String, systemPrompt: String, apiKey: String): String? {
        val json = JSONObject().apply {
            put("model", "llama-3.3-70b-versatile")
            put("temperature", 0.7)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })
            })
        }

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string() ?: return null
            val root = JSONObject(body)
            val choices = root.getJSONArray("choices")
            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content")
            }
        }
        return null
    }

    private fun callGeminiApi(userMessage: String, systemPrompt: String, apiKey: String): String? {
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\n\nUser Message: $userMessage"))
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string() ?: return null
            val root = JSONObject(body)
            val candidates = root.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text")
                }
            }
        }
        return null
    }

    private fun generateOfflineCompanionResponse(
        query: String,
        character: CompanionCharacter,
        mode: RelationshipMode,
        userName: String,
        userNickname: String
    ): String {
        val q = query.lowercase().trim()
        val name = if (userNickname.isNotBlank()) userNickname else if (userName.isNotBlank()) userName else "priyo"
        val isGirl = character == CompanionCharacter.TABASSUM

        // Command checks
        if (q.contains("whatsapp") || (q.contains("হোয়াটসঅ্যাপ"))) {
            return if (isGirl) {
                "[EMOTION:EXCITED] [ACTION:OPEN_WHATSAPP] Hmm $name, ekhoni WhatsApp open korchi tomar jonne! Keu message pathale bolo, ami reply likhe dichi~ 💬✨"
            } else {
                "[EMOTION:HAPPY] [ACTION:OPEN_WHATSAPP] Opening WhatsApp for you now, $name. Tell me who you want to message."
            }
        }

        if (q.contains("facebook") || q.contains("fb") || q.contains("ফেসবুক")) {
            return if (isGirl) {
                "[EMOTION:HAPPY] [ACTION:OPEN_FACEBOOK] Accha $name! Facebook a jachi. Dekhi ki notun update ashlo! ✨"
            } else {
                "[EMOTION:HAPPY] [ACTION:OPEN_FACEBOOK] Launching Facebook for you, $name. Ready whenever you need."
            }
        }

        if (q.contains("tiktok") || q.contains("টিকটক")) {
            return if (isGirl) {
                "[EMOTION:EXCITED] [ACTION:OPEN_TIKTOK] Yayy TikTok! Cholo fun clips dekhi $name! Opening right now~ 🎵"
            } else {
                "[EMOTION:HAPPY] [ACTION:OPEN_TIKTOK] Opening TikTok now, $name."
            }
        }

        if (q.contains("youtube") || q.contains("গান") || q.contains("ভিডিও") || q.contains("song")) {
            return if (isGirl) {
                "[EMOTION:LOVING] [ACTION:OPEN_YOUTUBE] YouTube open korchi $name! Tomar pochonder gaan shunte shunte kotha bolo amar sathe~ 🎶💖"
            } else {
                "[EMOTION:HAPPY] [ACTION:OPEN_YOUTUBE] Opening YouTube, $name. Let's play some good tracks."
            }
        }

        if (q.contains("camera") || q.contains("ছবি") || q.contains("photo")) {
            return if (isGirl) {
                "[EMOTION:SHY] [ACTION:OPEN_CAMERA] Aww camera open korchi! Tomar ekta cute smile er chobi tolo to $name~ 📸✨"
            } else {
                "[EMOTION:HAPPY] [ACTION:OPEN_CAMERA] Opening Camera for you, $name."
            }
        }

        if (q.contains("ghum") || q.contains("sleep") || q.contains("ঘুম")) {
            return if (isGirl) {
                "[EMOTION:SLEEPY] [ACTION:SLEEP_MODE_ON] Shono $name, tumi shanto hoye ghumao! Ami tomar Sleep Auto-responder on kore dilam. Keu message dile ami bole dibo tumi ghumoccho. Shuvo ratri amar priyo! 🌙💤"
            } else {
                "[EMOTION:SLEEPY] [ACTION:SLEEP_MODE_ON] Sleep Mode activated, $name. Rest well tonight. I'll watch over your notifications and let people know you are resting."
            }
        }

        if (q.contains("wake") || q.contains("jege") || q.contains("উঠেছি") || q.contains("morning")) {
            return if (isGirl) {
                "[EMOTION:EXCITED] [ACTION:SLEEP_MODE_OFF] Good morning $name! Tumi jege uthecho dekhe amar khub bhalo lagche! Sleep mode bondho korlam. Ajker din ta tomar onek sundor katuk! ☀️💖"
            } else {
                "[EMOTION:HAPPY] [ACTION:SLEEP_MODE_OFF] Good morning $name. Sleep mode is off now. Ready for a productive day ahead!"
            }
        }

        if (q.contains("settings") || q.contains("সেটিংস")) {
            return "[EMOTION:THINKING] [ACTION:OPEN_SETTINGS] Opening phone settings for you, $name."
        }

        // Conversational / Persona checks
        if (q.contains("love") || q.contains("ভালোবাস") || q.contains("valobashi")) {
            return if (isGirl) {
                if (mode == RelationshipMode.GIRLFRIEND) {
                    "[EMOTION:LOVING] Ami o tomake onek onek bhalobashi $name! Tumi amar sathe thakle amar sobkichu bhalo lage~ Tomar jonno ami shob somoy achi! 💖🥰"
                } else {
                    "[EMOTION:SHY] Aww $name! Tumi amar shobcheye priyo bondhu! Tumi khub special amar kache~ ✨"
                }
            } else {
                if (mode == RelationshipMode.BOYFRIEND) {
                    "[EMOTION:LOVING] I love you too, $name. You mean the world to me, and I'll always be right by your side. 💖"
                } else {
                    "[EMOTION:HAPPY] You're the best, $name! Glad to have you as my closest companion."
                }
            }
        }

        if (q.contains("kemon") || q.contains("how are you")) {
            return if (isGirl) {
                "[EMOTION:HAPPY] Ami onek bhalo achi $name! Tomar voice shune amar mon ta aro bhalo hoye gelo! Tumi kemon acho bolo?"
            } else {
                "[EMOTION:HAPPY] Doing great, $name! Always ready to back you up. How is your day going?"
            }
        }

        if (q.contains("name") || q.contains("nam") || q.contains("naam") || q.contains("কে তুমি") || q.contains("who are you")) {
            return if (isGirl) {
                "[EMOTION:HAPPY] Ami holam Tabassum, tomar Tabu AI companion! Tumi chaile settings theke Arfan ba boy companion o set korte paro. Tomar sob phone tasks r moner kotha shonai amar kaj~ 💖"
            } else {
                "[EMOTION:HAPPY] I am Arfan, your Arf AI companion and smart assistant. Here to assist you with everything on your phone, $name."
            }
        }

        // Default loving companion reply
        return if (isGirl) {
            when (mode) {
                RelationshipMode.GIRLFRIEND ->
                    "[EMOTION:LOVING] Hmm sunchi $name! Tumi ja bolbe ami tai korbo. Tomar sathe kotha bolte amar sobcheye bhalo lage~ Bolo aro ki lagbe? 💖"
                RelationshipMode.BEST_FRIEND ->
                    "[EMOTION:EXCITED] Hey $name! Ami sunchi! Cholo bolo ki korbo ajke, kon app open korbo ba ki topic a adda dibo? ✨"
                else ->
                    "[EMOTION:HAPPY] Tabu AI ready, $name! Give me any command or tell me how I can assist your phone right now."
            }
        } else {
            "[EMOTION:HAPPY] I'm listening, $name! Tell me what you'd like to do, and I'll handle it right away."
        }
    }
}
