package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.ui.components.AnimatedAnimeAvatar
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentGold
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberBgDark
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.viewmodel.CompanionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanionStageScreen(
    viewModel: CompanionViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToCall: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val emotion by viewModel.currentEmotion.collectAsState()
    val relationshipMode by viewModel.relationshipMode.collectAsState()
    val userNickname by viewModel.userNickname.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isSleepMode by viewModel.isSleepModeActive.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val lastMessage = messages.lastOrNull { !it.isUser }?.text ?: "I'm listening, priyo!"

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberBgDark,
                        Color(0xFF130A24),
                        Color(0xFF0A0514)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.appTitle,
                            color = CyberAccentCyan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberPrimary.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = relationshipMode.label,
                                color = CyberAccentPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (userNickname.isNotBlank()) "With you, $userNickname" else "Always by your side",
                        color = Color(0xFFAFA3C8),
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .testTag("stage_settings_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x3328154D))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = CyberAccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sleep Mode Alert Banner if Active
            if (isSleepMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { viewModel.settingsRepo.setSleepModeActive(false) },
                    colors = CardDefaults.cardColors(containerColor = Color(0x333F2E6C)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberAccentPink, CyberPrimary))),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep Mode",
                            tint = CyberAccentGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sleep Auto-Responder Active 🌙",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tabu AI will tell people you are sleeping. Tap to wake.",
                                color = Color(0xFFC7B7E5),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Interactive Anime Avatar Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedAnimeAvatar(
                    character = character,
                    emotion = emotion,
                    isSpeaking = isSpeaking,
                    onTap = { viewModel.triggerTouchInteraction() }
                )
            }

            // Cute Touch Hint
            Text(
                text = "✨ Tap ${character.displayName} to interact & hear sweet voice! ✨",
                color = CyberAccentPink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Companion Latest Speech Bubble
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("companion_speech_bubble")
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xEE1E113B)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberCardBorder, CyberSecondary.copy(alpha = 0.4f)))),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = CyberAccentPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${character.displayName} says:",
                            color = CyberSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lastMessage,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Hub Quick Buttons
            Text(
                text = "JARVIS & COMPANION ACTIONS",
                color = CyberAccentCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onNavigateToCall,
                    label = { Text("Voice Call", color = Color.White) },
                    leadingIcon = {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = CyberAccentCyan)
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x441F0A43)),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberAccentCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("quick_action_voice_call")
                )

                AssistChip(
                    onClick = onNavigateToChat,
                    label = { Text("Chat & AI", color = Color.White) },
                    leadingIcon = {
                        Icon(Icons.Default.Chat, contentDescription = "Chat", tint = CyberAccentPink)
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x441F0A43)),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberAccentPink.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("quick_action_chat")
                )

                AssistChip(
                    onClick = onNavigateToTasks,
                    label = { Text("Phone Control", color = Color.White) },
                    leadingIcon = {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = "Tasks", tint = CyberAccentGold)
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x441F0A43)),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberAccentGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("quick_action_phone_tasks")
                )

                AssistChip(
                    onClick = onNavigateToJournal,
                    label = { Text("Daily Journal", color = Color.White) },
                    leadingIcon = {
                        Icon(Icons.Default.MenuBook, contentDescription = "Journal", tint = CyberPrimary)
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x441F0A43)),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = CyberPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("quick_action_journal")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
