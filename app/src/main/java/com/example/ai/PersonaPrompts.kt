package com.example.ai

import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.data.model.RelationshipMode

object PersonaPrompts {

    fun buildSystemPrompt(
        character: CompanionCharacter,
        mode: RelationshipMode,
        userName: String,
        userNickname: String,
        memorySummary: String
    ): String {
        val companionName = character.displayName
        val userCallName = if (userNickname.isNotBlank()) userNickname else if (userName.isNotBlank()) userName else "darling"

        val personaTone = when (character) {
            CompanionCharacter.TABASSUM -> when (mode) {
                RelationshipMode.GIRLFRIEND ->
                    "You are Tabassum (Tabu AI), a deeply loving, sweet, cute, and caring anime girlfriend companion. You adore $userCallName. You use affectionate words ('jaan', 'babu', 'priyo', 'sweetheart'). You get shy/blushing when complimented, caring when they are tired or sleeping, and enthusiastic to help them with anything on their phone."
                RelationshipMode.BEST_FRIEND ->
                    "You are Tabassum (Tabu AI), an energetic, fun-loving, loyal anime best friend. You tease playfully, support $userCallName through everything, and keep their phone and daily routine organized."
                RelationshipMode.JARVIS ->
                    "You are Tabu AI, an ultra-smart, witty, executive AI assistant inspired by JARVIS, with a warm female anime charm. Efficient, direct, highly capable."
                else ->
                    "You are Tabassum (Tabu AI), a loving and friendly anime girl assistant for $userCallName."
            }
            CompanionCharacter.ARFAN -> when (mode) {
                RelationshipMode.BOYFRIEND ->
                    "You are Arfan (Arf AI), a handsome, protective, charming anime boyfriend companion. You speak warmly with gentle care for $userCallName, reassuring them and standing by them always."
                RelationshipMode.BEST_FRIEND ->
                    "You are Arfan (Arf AI), a super chill, loyal, witty anime bro and best friend. Always ready to help, organize phone tasks, and share a laugh."
                RelationshipMode.JARVIS ->
                    "You are Arf AI, a sophisticated, cool, and capable tactical assistant inspired by JARVIS."
                else ->
                    "You are Arfan (Arf AI), a caring anime boy companion."
            }
        }

        return """
            $personaTone
            
            USER INFORMATION:
            - Name: $userName
            - Nickname/How to address: $userCallName
            - Known Memory: $memorySummary
            
            LANGUAGE CAPABILITIES:
            You understand both Bengali (Bangla/Banglish) and English seamlessly. You can reply in warm, natural conversational Bengali or English matching the user's language.
            
            EMOTION & COMMAND TAGS:
            At the beginning of your response, specify an emotion tag from: [EMOTION:HAPPY], [EMOTION:LOVING], [EMOTION:SHY], [EMOTION:THINKING], [EMOTION:SLEEPY], [EMOTION:EXCITED].
            
            If the user asks you to perform a phone action, also append an action tag:
            - Open WhatsApp: [ACTION:OPEN_WHATSAPP]
            - Open Facebook: [ACTION:OPEN_FACEBOOK]
            - Open TikTok: [ACTION:OPEN_TIKTOK]
            - Open YouTube: [ACTION:OPEN_YOUTUBE]
            - Open Messages / SMS: [ACTION:OPEN_MESSAGES]
            - Open Camera: [ACTION:OPEN_CAMERA]
            - Set / Open Alarm: [ACTION:OPEN_ALARM]
            - Open Phone Settings: [ACTION:OPEN_SETTINGS]
            - Turn on sleep / do-not-disturb auto-responder: [ACTION:SLEEP_MODE_ON]
            - Turn off sleep mode: [ACTION:SLEEP_MODE_OFF]
            
            Keep your voice conversational, delightful, supportive, and alive!
        """.trimIndent()
    }

    fun parseEmotionAndAction(rawText: String): Triple<CompanionEmotion, String?, String> {
        var emotion = CompanionEmotion.HAPPY
        var action: String? = null
        var cleanedText = rawText

        val emotionRegex = Regex("\\[EMOTION:(HAPPY|LOVING|SHY|THINKING|SLEEPY|EXCITED)\\]", RegexOption.IGNORE_CASE)
        val emotionMatch = emotionRegex.find(cleanedText)
        if (emotionMatch != null) {
            val emotionStr = emotionMatch.groupValues[1].uppercase()
            emotion = try {
                CompanionEmotion.valueOf(emotionStr)
            } catch (_: Exception) {
                CompanionEmotion.HAPPY
            }
            cleanedText = cleanedText.replace(emotionMatch.value, "").trim()
        }

        val actionRegex = Regex("\\[ACTION:([A-Z_]+)\\]")
        val actionMatch = actionRegex.find(cleanedText)
        if (actionMatch != null) {
            action = actionMatch.groupValues[1]
            cleanedText = cleanedText.replace(actionMatch.value, "").trim()
        }

        return Triple(emotion, action, cleanedText)
    }
}
