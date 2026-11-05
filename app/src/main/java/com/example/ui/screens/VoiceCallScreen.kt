package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CompanionCharacter
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberBgDark
import com.example.ui.theme.CyberPrimary
import com.example.ui.viewmodel.CompanionViewModel

@Composable
fun VoiceCallScreen(
    viewModel: CompanionViewModel,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onEndCall() }

    val character by viewModel.character.collectAsState()
    val emotion by viewModel.currentEmotion.collectAsState()
    val userNickname by viewModel.userNickname.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val amplitude by viewModel.audioAmplitude.collectAsState()
    val lastSpokenText by viewModel.voiceManager.lastSpokenText.collectAsState()

    val imageRes = if (character == CompanionCharacter.TABASSUM) {
        R.drawable.tabassum_companion
    } else {
        R.drawable.arfan_companion
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F061F),
                        Color(0xFF1B0C36),
                        CyberBgDark
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "VOICE CALL CONNECTED",
                    color = CyberAccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = character.displayName,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (isSpeaking) "Speaking with sweet voice... 💖" else if (isListening) "Listening to you, $userNickname~" else "Connected",
                    color = if (isListening) CyberAccentCyan else Color(0xFFC7B7E5),
                    fontSize = 14.sp
                )
            }

            // Central Companion Hologram Card
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(24.dp, CircleShape)
                    .clip(CircleShape)
                    .border(3.dp, if (isSpeaking) CyberAccentPink else CyberAccentCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Companion Avatar Call",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Real-time audio waveform visualizer
            WaveformVisualizer(
                isActive = isSpeaking || isListening,
                amplitude = amplitude,
                barCount = 24,
                maxHeight = 64.dp,
                barColor = if (isSpeaking) CyberAccentPink else CyberAccentCyan
            )

            // Transcript Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x3328154D)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberPrimary.copy(alpha = 0.4f), CyberAccentCyan.copy(alpha = 0.4f)))),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (lastSpokenText.isNotBlank()) "\"$lastSpokenText\"" else "Speak now... Say \"Tabu open WhatsApp\" or anything you like!",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                )
            }

            // Call Controls (Mic, Hang Up, Speaker)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic button
                IconButton(
                    onClick = {
                        if (isListening) viewModel.voiceManager.stopListening() else viewModel.voiceManager.startListening()
                    },
                    modifier = Modifier
                        .testTag("call_mic_toggle")
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (isListening) CyberAccentCyan else Color(0x4428154D))
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Microphone",
                        tint = if (isListening) Color.Black else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Hang Up Button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .testTag("call_end_button")
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE63946))
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Speaker replay button
                IconButton(
                    onClick = {
                        viewModel.triggerTouchInteraction()
                    },
                    modifier = Modifier
                        .testTag("call_speaker_button")
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0x4428154D))
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speaker",
                        tint = CyberAccentPink,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
