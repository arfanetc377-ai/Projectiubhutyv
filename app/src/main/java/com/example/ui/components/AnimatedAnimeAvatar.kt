package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CompanionCharacter
import com.example.data.model.CompanionEmotion
import com.example.ui.theme.CyberAccentCyan
import com.example.ui.theme.CyberAccentGold
import com.example.ui.theme.CyberAccentPink
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun AnimatedAnimeAvatar(
    character: CompanionCharacter,
    emotion: CompanionEmotion,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "anime_character_animation")

    // Breathing float animation
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Gentle scale pulse
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Holographic aura rotation
    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura"
    )

    // Speaking wave amplitude
    val speakingPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse"
    )

    val imageRes = if (character == CompanionCharacter.TABASSUM) {
        R.drawable.tabassum_companion
    } else {
        R.drawable.arfan_companion
    }

    val glowColor = when (emotion) {
        CompanionEmotion.LOVING, CompanionEmotion.SHY -> CyberAccentPink
        CompanionEmotion.EXCITED -> CyberAccentGold
        CompanionEmotion.SLEEPY -> Color(0xFF6C757D)
        CompanionEmotion.THINKING -> CyberPrimary
        else -> if (character == CompanionCharacter.TABASSUM) CyberSecondary else CyberAccentCyan
    }

    Box(
        modifier = modifier
            .testTag("animated_anime_character_container")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        // Holographic cyber background energy ring
        Canvas(
            modifier = Modifier
                .size(340.dp)
                .scale(if (isSpeaking) speakingPulse else scalePulse)
        ) {
            val centerOffset = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2.3f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = if (isSpeaking) 0.35f else 0.18f),
                        glowColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = radius * 1.3f
                ),
                radius = radius * 1.2f,
                center = centerOffset
            )

            // Sci-fi segmented rings
            drawCircle(
                color = glowColor.copy(alpha = 0.4f),
                radius = radius,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = CyberPrimary.copy(alpha = 0.25f),
                radius = radius * 0.85f,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Animated Anime Character Illustration Layer
        Box(
            modifier = Modifier
                .offset { IntOffset(0, breathingOffset.roundToInt()) }
                .scale(if (isSpeaking) speakingPulse * 0.98f else scalePulse)
                .size(width = 290.dp, height = 440.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "${character.displayName} Anime Companion",
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("companion_avatar_image"),
                contentScale = ContentScale.Crop
            )

            // Blushing / Emotional Tint Layer
            if (emotion == CompanionEmotion.SHY || emotion == CompanionEmotion.LOVING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CyberAccentPink.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Top Status Emotion Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xDD150B28))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${emotion.emoji} ${emotion.label}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Speaking indicator banner if active
            if (isSpeaking) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberPrimary.copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Talking to you...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
