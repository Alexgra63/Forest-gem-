package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.ui.components.ForestFireflies
import com.example.ui.theme.*

@Composable
fun MainMenuScreen(
    playerCoins: Int,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onPlayCampaign: () -> Unit,
    onPlayZen: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TitleGlow")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Enchanted forest background
        Image(
            painter = painterResource(id = R.drawable.forest_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark atmospheric gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ForestDarkBackground.copy(alpha = 0.65f),
                            ForestDarkBackground.copy(alpha = 0.45f),
                            ForestDarkBackground.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Floating ambient fireflies
        ForestFireflies()

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Treasury Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ForestDarkBackground.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGold),
                    modifier = Modifier.testTag("coins_treasury")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$playerCoins Coins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForestGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Title Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(titleScale)
            ) {
                Text(
                    text = "GEM FOREST",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestEmeraldLight,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "SAGA",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enchanted Coins & Mystic Gems",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StarWhite.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Menu Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Play Campaign
                Button(
                    onClick = onPlayCampaign,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("play_campaign_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestEmeraldDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ForestEmeraldLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = ForestGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY CAMPAIGN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                // Endless Zen
                Button(
                    onClick = onPlayZen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_zen_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestSurfaceVariant.copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SapphireGem)
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Endless Zen Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StarWhite
                    )
                }

                // Magical Treasury / Shop
                OutlinedButton(
                    onClick = onOpenShop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_shop_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGold),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ForestDarkBackground.copy(alpha = 0.7f)
                    )
                ) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Power-up Treasury",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGold
                    )
                }

                // How to Play Guide
                TextButton(
                    onClick = onOpenHowToPlay,
                    modifier = Modifier.testTag("how_to_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "How to Play & Combos",
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Bottom Audio / Haptics bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ForestDarkBackground.copy(alpha = 0.8f))
                        .border(1.dp, ForestCardBorder, CircleShape)
                        .testTag("toggle_sound_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = if (soundEnabled) ForestGold else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    onClick = onToggleHaptics,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ForestDarkBackground.copy(alpha = 0.8f))
                        .border(1.dp, ForestCardBorder, CircleShape)
                        .testTag("toggle_haptics_button")
                ) {
                    Icon(
                        imageVector = if (hapticsEnabled) Icons.Default.Vibration else Icons.Default.Smartphone,
                        contentDescription = "Toggle Haptics",
                        tint = if (hapticsEnabled) ForestEmeraldLight else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
