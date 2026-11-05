package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CompanionCharacter
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberPrimary

@Composable
fun FloatingMiniCompanion(
    visible: Boolean,
    character: CompanionCharacter,
    isSpeaking: Boolean,
    isListening: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_float")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val imageRes = if (character == CompanionCharacter.TABASSUM) {
        R.drawable.tabassum_companion
    } else {
        R.drawable.arfan_companion
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .testTag("floating_mini_companion_bubble")
                .shadow(16.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xEE1C0F38),
                            Color(0xEE2A124F)
                        )
                    )
                )
                .border(1.5.dp, CyberAccentCyan.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                .clickable { onTap() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini avatar circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .scale(if (isSpeaking) pulse else 1f)
                        .clip(CircleShape)
                        .border(1.5.dp, CyberAccentPink, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Mini Companion",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name and status
                androidx.compose.foundation.layout.Column {
                    Text(
                        text = character.displayName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSpeaking) "Speaking..." else if (isListening) "Listening~" else "Tap to talk",
                        color = if (isListening) CyberAccentCyan else Color(0xFFCBB6E8),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Icon indicator
                val icon = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic
                val tint = if (isListening) CyberAccentCyan else if (isSpeaking) CyberAccentPink else Color(0xFF9F8AC2)
                Icon(
                    imageVector = icon,
                    contentDescription = "Voice Status",
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
