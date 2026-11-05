package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.components.FloatingMiniCompanion
import com.example.ui.screens.AutomationHubScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CompanionStageScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.OnboardingDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceCallScreen
import com.example.ui.theme.TabuAiTheme
import com.example.ui.viewmodel.CompanionViewModel

enum class AppScreen {
    STAGE,
    CHAT,
    CALL,
    TASKS,
    JOURNAL,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: CompanionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TabuAiTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: CompanionViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.STAGE) }

    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val isCallActive by viewModel.isCallActive.collectAsState()
    val floatingEnabled by viewModel.floatingCompanionEnabled.collectAsState()
    val character by viewModel.character.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val lastActionNotice by viewModel.lastActionNotice.collectAsState()

    // Audio record permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Microphone enabled! You can now talk to ${character.displayName}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(lastActionNotice) {
        lastActionNotice?.let { notice ->
            Toast.makeText(context, notice, Toast.LENGTH_SHORT).show()
            viewModel.clearActionNotice()
        }
    }

    LaunchedEffect(isCallActive) {
        if (isCallActive && currentScreen != AppScreen.CALL) {
            currentScreen = AppScreen.CALL
        } else if (!isCallActive && currentScreen == AppScreen.CALL) {
            currentScreen = AppScreen.STAGE
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.STAGE -> CompanionStageScreen(
                        viewModel = viewModel,
                        onNavigateToChat = { currentScreen = AppScreen.CHAT },
                        onNavigateToCall = {
                            viewModel.startVoiceCall()
                            currentScreen = AppScreen.CALL
                        },
                        onNavigateToTasks = { currentScreen = AppScreen.TASKS },
                        onNavigateToJournal = { currentScreen = AppScreen.JOURNAL },
                        onNavigateToSettings = { currentScreen = AppScreen.SETTINGS }
                    )

                    AppScreen.CHAT -> ChatScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STAGE }
                    )

                    AppScreen.CALL -> VoiceCallScreen(
                        viewModel = viewModel,
                        onEndCall = {
                            viewModel.endVoiceCall()
                            currentScreen = AppScreen.STAGE
                        }
                    )

                    AppScreen.TASKS -> AutomationHubScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STAGE }
                    )

                    AppScreen.JOURNAL -> JournalScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STAGE }
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.STAGE }
                    )
                }
            }

            // Floating Mini Companion Bubble (visible across sub-screens if enabled)
            val shouldShowFloating = floatingEnabled &&
                    currentScreen != AppScreen.STAGE &&
                    currentScreen != AppScreen.CALL

            FloatingMiniCompanion(
                visible = shouldShowFloating,
                character = character,
                isSpeaking = isSpeaking,
                isListening = isListening,
                onTap = {
                    if (isSpeaking) {
                        viewModel.voiceManager.stopSpeaking()
                    } else if (isListening) {
                        viewModel.voiceManager.stopListening()
                    } else {
                        viewModel.triggerTouchInteraction()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
            )

            // First Launch Onboarding Dialog
            if (!hasCompletedOnboarding) {
                OnboardingDialog(
                    onComplete = { name, nickname, mode ->
                        viewModel.completeOnboarding(name, nickname, mode)
                    }
                )
            }
        }
    }
}
