package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
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
            animation = tween(2400, easing = FastOutSlowInEasing),
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
                            ForestDarkBackground.copy(alpha = 0.55f),
                            ForestDarkBackground.copy(alpha = 0.35f),
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
                .padding(horizontal = 18.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Treasury Bar with Golden Coin Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ForestDarkBackground.copy(alpha = 0.88f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGold),
                    modifier = Modifier.testTag("coins_treasury")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_forest_gold_coin),
                            contentDescription = "Gold Coin",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$playerCoins Coins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ForestGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // 3D Carved Wooden "Forest Saga" Title Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(titleScale)
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.title_forest_gems),
                    contentDescription = "Forest Saga",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(135.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enchanted Coins & Mystic Gems",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = ForestEmeraldLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }

            // The 3 Wooden Banner Buttons (Cut out backgrounds from uploaded asset)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 390.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Button 1: Play Campaign (using cut-out background 1)
                WoodBannerButton(
                    title = "PLAY CAMPAIGN",
                    subtitle = "Endless Climbing Grapevine",
                    iconResource = R.drawable.ic_forest_gold_coin,
                    backgroundResource = R.drawable.btn_wood_banner_1,
                    onClick = onPlayCampaign,
                    testTag = "play_campaign_button",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                )

                // Button 2: Endless Zen (using cut-out background 2)
                WoodBannerButton(
                    title = "ENDLESS ZEN",
                    subtitle = "Relaxing Match-3 Magic",
                    iconResource = R.drawable.gem_emerald_png,
                    backgroundResource = R.drawable.btn_wood_banner_2,
                    onClick = onPlayZen,
                    testTag = "play_zen_button",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )

                // Button 3: Power-up Treasury (using cut-out background 3)
                WoodBannerButton(
                    title = "POWER-UP TREASURY",
                    subtitle = "Unlock Mythical Powers",
                    iconResource = R.drawable.ic_forest_gold_coin,
                    backgroundResource = R.drawable.btn_wood_banner_3,
                    onClick = onOpenShop,
                    testTag = "open_shop_button",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                )

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
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ForestDarkBackground.copy(alpha = 0.85f))
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ForestDarkBackground.copy(alpha = 0.85f))
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

/**
 * Cut-out 3D wooden banner button featuring moss, golden trims, and leafy vines.
 */
@Composable
fun WoodBannerButton(
    title: String,
    subtitle: String? = null,
    iconResource: Int? = null,
    backgroundResource: Int,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 48.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Cut-out wooden banner PNG with leafy vines and gold border
        Image(
            painter = painterResource(id = backgroundResource),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Text & Icon content centered within the wooden board area
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconResource != null) {
                Image(
                    painter = painterResource(id = iconResource),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.9f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.2.sp,
                    fontSize = 15.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        ),
                        color = ForestGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
