package com.example

import com.example.ai.PersonaPrompts
import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.data.model.RelationshipMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleCompanionUnitTest {

    @Test
    fun testParseEmotionAndAction() {
        val rawInput = "[EMOTION:LOVING] [ACTION:OPEN_WHATSAPP] Hello priyo, opening WhatsApp now!"
        val (emotion, action, text) = PersonaPrompts.parseEmotionAndAction(rawInput)

        assertEquals(CompanionEmotion.LOVING, emotion)
        assertEquals("OPEN_WHATSAPP", action)
        assertEquals("Hello priyo, opening WhatsApp now!", text)
    }

    @Test
    fun testSystemPromptGeneration() {
        val prompt = PersonaPrompts.buildSystemPrompt(
            character = CompanionCharacter.TABASSUM,
            mode = RelationshipMode.GIRLFRIEND,
            userName = "Arfan",
            userNickname = "Jaan",
            memorySummary = "User loves anime"
        )
        assertNotNull(prompt)
        assert(prompt.contains("Tabassum"))
        assert(prompt.contains("Jaan"))
    }
}
