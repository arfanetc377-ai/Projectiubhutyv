package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanionCharacter
import com.example.data.model.RelationshipMode
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentGold
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberBgDark
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.viewmodel.CompanionViewModel

@Composable
fun SettingsScreen(
    viewModel: CompanionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val activeCharacter by viewModel.character.collectAsState()
    val activeMode by viewModel.relationshipMode.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userNickname by viewModel.userNickname.collectAsState()
    val groqKey by viewModel.settingsRepo.groqApiKey.collectAsState()
    val geminiKey by viewModel.settingsRepo.geminiApiKey.collectAsState()
    val floatingEnabled by viewModel.floatingCompanionEnabled.collectAsState()

    var editName by remember(userName) { mutableStateOf(userName) }
    var editNickname by remember(userNickname) { mutableStateOf(userNickname) }
    var editGroqKey by remember(groqKey) { mutableStateOf(groqKey) }
    var editGeminiKey by remember(geminiKey) { mutableStateOf(geminiKey) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberBgDark,
                        Color(0xFF150B2C),
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
                modifier = Modifier.testTag("settings_back_button")
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
                    text = "Companion & AI Settings",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Customize character, relationship & dual keys",
                    color = CyberSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. CHARACTER SWITCHER (Tabassum vs Arfan)
        Text(
            text = "CHOOSE COMPANION CHARACTER",
            color = CyberAccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tabassum (Tabu AI)
            val isTabu = activeCharacter == CompanionCharacter.TABASSUM
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.settingsRepo.setCharacter(CompanionCharacter.TABASSUM)
                        viewModel.voiceManager.speak("Assalamu Alaikum! Ami Tabassum. Tomar Tabu AI ekhon active~ 💖", CompanionCharacter.TABASSUM)
                    },
                colors = CardDefaults.cardColors(containerColor = if (isTabu) Color(0x442C1254) else Color(0x221B0F33)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (isTabu) listOf(CyberAccentPink, CyberPrimary) else listOf(Color(0x33B388FF), Color.Transparent)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👧", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tabassum",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tabu AI (Girl)",
                        color = CyberAccentPink,
                        fontSize = 12.sp
                    )
                    if (isTabu) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberAccentPink)
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Arfan (Arf AI)
            val isArfan = activeCharacter == CompanionCharacter.ARFAN
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.settingsRepo.setCharacter(CompanionCharacter.ARFAN)
                        viewModel.voiceManager.speak("Hello! I am Arfan. Arf AI is now active and ready.", CompanionCharacter.ARFAN)
                    },
                colors = CardDefaults.cardColors(containerColor = if (isArfan) Color(0x44122C54) else Color(0x221B0F33)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        if (isArfan) listOf(CyberAccentCyan, CyberSecondary) else listOf(Color(0x3300F5D4), Color.Transparent)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👦", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Arfan",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Arf AI (Boy)",
                        color = CyberAccentCyan,
                        fontSize = 12.sp
                    )
                    if (isArfan) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberAccentCyan)
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. RELATIONSHIP MODE
        Text(
            text = "RELATIONSHIP DYNAMICS",
            color = CyberAccentGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        val modes = if (activeCharacter == CompanionCharacter.TABASSUM) {
            listOf(
                RelationshipMode.GIRLFRIEND to "Girlfriend Mode (Sweet, romantic & caring)",
                RelationshipMode.BEST_FRIEND to "Best Friend Mode (Supportive, cheerful buddy)",
                RelationshipMode.JARVIS to "JARVIS Mode (Executive AI phone assistant)"
            )
        } else {
            listOf(
                RelationshipMode.BOYFRIEND to "Boyfriend Mode (Warm, protective & charming)",
                RelationshipMode.BEST_FRIEND to "Best Friend Mode (Loyal bro & helper)",
                RelationshipMode.JARVIS to "JARVIS Mode (Tactical AI phone assistant)"
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            modes.forEach { (mode, desc) ->
                val isSelected = activeMode == mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.settingsRepo.setRelationshipMode(mode) },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0x333F1F6E) else Color(0x221B0F33)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isSelected) listOf(CyberAccentPink, CyberPrimary) else listOf(Color(0x22B388FF), Color.Transparent)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CyberPrimary else Color(0x223F1F6E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (mode == RelationshipMode.GIRLFRIEND || mode == RelationshipMode.BOYFRIEND) Icons.Default.Favorite else Icons.Default.Person,
                                contentDescription = mode.label,
                                tint = if (isSelected) Color.Black else CyberAccentPink
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode.label,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = desc,
                                color = Color(0xFFAFA3C8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. DUAL AI ENGINE KEYS (Groq + Gemini)
        Text(
            text = "DUAL AI ENGINES (GROQ + GEMINI)",
            color = CyberSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Both keys work together: Groq for lightning-fast voice replies & Gemini for complex phone tasks.",
            color = Color(0xFFC7B7E5),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x221B0F33)),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberSecondary.copy(alpha = 0.4f), CyberPrimary.copy(alpha = 0.4f)))),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Groq Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Groq API Key (Fast Reply)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.groq.com/keys"))
                                context.startActivity(intent)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Get Key", color = CyberAccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Get Key", tint = CyberAccentCyan, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = editGroqKey,
                    onValueChange = {
                        editGroqKey = it
                        viewModel.settingsRepo.setGroqApiKey(it)
                    },
                    placeholder = { Text("gsk_...", color = Color(0xFF8E7FA8), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_groq_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccentCyan,
                        unfocusedBorderColor = Color(0x33B388FF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gemini Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gemini API Key (Deep Intelligence)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                                context.startActivity(intent)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Get Key", color = CyberAccentPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Get Key", tint = CyberAccentPink, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = editGeminiKey,
                    onValueChange = {
                        editGeminiKey = it
                        viewModel.settingsRepo.setGeminiApiKey(it)
                    },
                    placeholder = { Text("AIzaSy...", color = Color(0xFF8E7FA8), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_gemini_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberAccentPink,
                        unfocusedBorderColor = Color(0x33B388FF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. USER PROFILE
        Text(
            text = "YOUR PROFILE INFORMATION",
            color = CyberAccentCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = editName,
            onValueChange = {
                editName = it
                viewModel.settingsRepo.setUserProfile(it, editNickname)
            },
            label = { Text("Your Full Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccentCyan,
                unfocusedBorderColor = Color(0x33B388FF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = editNickname,
            onValueChange = {
                editNickname = it
                viewModel.settingsRepo.setUserProfile(editName, it)
            },
            label = { Text("Nickname (How companion should call you)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccentPink,
                unfocusedBorderColor = Color(0x33B388FF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 5. FLOATING MINI COMPANION TOGGLE
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0x221B0F33)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "On-Screen Floating Companion Bubble",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Shows mini avatar floating on screen for quick tap & wake",
                        color = Color(0xFFAFA3C8),
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = floatingEnabled,
                    onCheckedChange = { viewModel.settingsRepo.setFloatingCompanionEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberAccentCyan,
                        checkedTrackColor = CyberPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
