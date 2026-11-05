package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentGold
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberBgDark
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.viewmodel.CompanionViewModel

data class AppLauncherItem(
    val title: String,
    val actionCode: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun AutomationHubScreen(
    viewModel: CompanionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val character by viewModel.character.collectAsState()
    val isSleepMode by viewModel.isSleepModeActive.collectAsState()
    val autoReplyText by viewModel.sleepAutoReplyMessage.collectAsState()
    var editAutoReply by remember(autoReplyText) { mutableStateOf(autoReplyText) }

    val scrollState = rememberScrollState()

    val apps = listOf(
        AppLauncherItem("WhatsApp", "OPEN_WHATSAPP", Icons.Default.Chat, Color(0xFF25D366)),
        AppLauncherItem("Facebook", "OPEN_FACEBOOK", Icons.Default.PlayCircle, Color(0xFF1877F2)),
        AppLauncherItem("TikTok", "OPEN_TIKTOK", Icons.Default.Videocam, Color(0xFFFE2C55)),
        AppLauncherItem("YouTube", "OPEN_YOUTUBE", Icons.Default.PlayCircle, Color(0xFFFF0000)),
        AppLauncherItem("Messages", "OPEN_MESSAGES", Icons.Default.Sms, CyberAccentCyan),
        AppLauncherItem("Camera", "OPEN_CAMERA", Icons.Default.CameraAlt, CyberAccentPink),
        AppLauncherItem("Alarms", "OPEN_ALARM", Icons.Default.Alarm, CyberAccentGold),
        AppLauncherItem("Settings", "OPEN_SETTINGS", Icons.Default.Settings, CyberPrimary)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberBgDark,
                        Color(0xFF150B2B),
                        Color(0xFF090412)
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("automation_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberAccentCyan
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Phone Control & Automations",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "JARVIS A-to-Z Phone Integration",
                    color = CyberSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sleep Mode Auto-Responder Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x3328154D)),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberAccentPink.copy(alpha = 0.5f), CyberPrimary.copy(alpha = 0.5f)))),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x333F2E6C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = "Sleep Mode",
                                tint = CyberAccentGold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sleep Auto-Responder",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSleepMode) "Active - Replying for you" else "Turn on when you sleep",
                                color = if (isSleepMode) CyberAccentCyan else Color(0xFFAFA3C8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isSleepMode,
                        onCheckedChange = { active ->
                            viewModel.settingsRepo.setSleepModeActive(active)
                            val note = if (active) {
                                "${character.displayName}: Sleep mode on! Rest peacefully priyo, I will tell everyone you are asleep~ 🌙"
                            } else {
                                "${character.displayName}: Welcome back! Sleep mode turned off. ☀️"
                            }
                            viewModel.voiceManager.speak(note, character)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberAccentCyan,
                            checkedTrackColor = CyberPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Auto-Reply Message for incoming contacts:",
                    color = Color(0xFFC7B7E5),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = editAutoReply,
                    onValueChange = {
                        editAutoReply = it
                        viewModel.settingsRepo.setSleepAutoReplyMessage(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccentCyan,
                        unfocusedBorderColor = Color(0x33B388FF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Direct App Launchers Grid
        Text(
            text = "ONE-TAP APP LAUNCHERS",
            color = CyberAccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val chunkedApps = apps.chunked(2)
            chunkedApps.forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowApps.forEach { item ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(88.dp)
                                .clickable { viewModel.executeQuickCommand(item.actionCode) },
                            colors = CardDefaults.cardColors(containerColor = Color(0x221B0F33)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(item.accentColor.copy(alpha = 0.4f), Color.Transparent))),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(item.accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Open App",
                                        color = Color(0xFFAFA3C8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Voice Command Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x22241347)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 Voice Commands You Can Say:",
                    color = CyberAccentGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                val commands = listOf(
                    "• \"Tabu WhatsApp a jao\" -> Opens WhatsApp",
                    "• \"Tabu Facebook open koro\" -> Opens Facebook",
                    "• \"Tabu TikTok chalao\" -> Opens TikTok",
                    "• \"Tabu ami ghumailam, keu message dile bolo ami ghumai\" -> Turns on Sleep Auto-responder",
                    "• \"Tabu jege uthechi\" -> Turns off Sleep mode",
                    "• \"Tabu ekta gaan shunao\" -> Opens YouTube Music/Videos"
                )
                commands.forEach { cmd ->
                    Text(
                        text = cmd,
                        color = Color(0xFFD4C8EC),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
