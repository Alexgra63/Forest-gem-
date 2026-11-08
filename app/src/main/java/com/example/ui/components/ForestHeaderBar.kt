package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LevelConfig
import com.example.ui.theme.*

@Composable
fun ForestHeaderBar(
    levelConfig: LevelConfig,
    score: Int,
    movesRemaining: Int,
    coinsCollected: Int,
    mossCleared: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation when moves are running critically low (<= 5)
    val infiniteTransition = rememberInfiniteTransition(label = "LowMovesPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (movesRemaining in 1..5) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val movesBadgeColor by animateColorAsState(
        targetValue = when {
            movesRemaining <= 3 -> RubyGem
            movesRemaining <= 6 -> TopazGem
            else -> ForestEmeraldDark
        },
        label = "movesColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ForestDarkBackground.copy(alpha = 0.95f),
                        ForestSurface.copy(alpha = 0.90f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = ForestCardBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Top row: Pause Button, Level Title, Moves Remaining
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WoodCircleButton(
                onClick = onPauseClick,
                modifier = Modifier.size(46.dp),
                testTag = "pause_button"
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Game",
                    tint = ForestGold,
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WoodCircleBadge(modifier = Modifier.size(38.dp)) {
                    Text(
                        text = "${levelConfig.levelNumber}",
                        color = Color(0xFFFFF6D8),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "LEVEL ${levelConfig.levelNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = levelConfig.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = StarWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }

            // Moves Remaining Badge
            WoodCircleBadge(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(52.dp)
                    .testTag("moves_counter")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "$movesRemaining",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (movesRemaining <= 5) RubyGem else Color(0xFFFFF6D8),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "MOVES",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Middle Row: Score Bar with 3 Star Indicators
        val progress = (score.toFloat() / levelConfig.threeStarScore.toFloat()).coerceIn(0f, 1f)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ForestEmeraldLight,
                    fontWeight = FontWeight.Bold
                )

                // 3 Stars based on thresholds
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val star1 = score >= levelConfig.oneStarScore
                    val star2 = score >= levelConfig.twoStarScore
                    val star3 = score >= levelConfig.threeStarScore
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 1",
                        tint = if (star1) ForestGold else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 2",
                        tint = if (star2) ForestGold else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star 3",
                        tint = if (star3) ForestGold else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Score progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ForestDarkBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ForestEmeraldDark, ForestEmerald, ForestGold)
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Row: Goals Badges (Target, Coins & Moss)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target Score Goal
            val scoreMet = score >= levelConfig.targetScore
            GoalPill(
                imageRes = R.drawable.ic_target_bullseye,
                label = "Target",
                status = if (scoreMet) "Done!" else "$score/${levelConfig.targetScore}",
                isComplete = scoreMet,
                modifier = Modifier.weight(1f)
            )

            // Coins Goal
            if (levelConfig.targetCoins > 0) {
                val coinsMet = coinsCollected >= levelConfig.targetCoins
                GoalPill(
                    imageRes = R.drawable.ic_forest_gold_coin,
                    label = "Coins",
                    status = "$coinsCollected/${levelConfig.targetCoins}",
                    isComplete = coinsMet,
                    modifier = Modifier.weight(1f)
                )
            }

            // Moss Goal
            if (levelConfig.targetMoss > 0) {
                val mossMet = mossCleared >= levelConfig.targetMoss
                GoalPill(
                    icon = "🌿",
                    label = "Moss",
                    status = "$mossCleared/${levelConfig.targetMoss}",
                    isComplete = mossMet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GoalPill(
    imageRes: Int? = null,
    icon: String? = null,
    label: String,
    status: String,
    isComplete: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(2141f / 642f),
        contentAlignment = Alignment.Center
    ) {
        // Wood rounded plank background (un-stretched 2141:642 aspect ratio)
        Image(
            painter = painterResource(id = R.drawable.btn_wood_rounded),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else if (icon != null) {
                Text(text = icon, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = "$label: $status",
                color = if (isComplete) ForestEmeraldLight else Color(0xFFFFF6D8),
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp,
                maxLines = 1,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF1E0F04),
                        offset = Offset(1f, 1.5f),
                        blurRadius = 3f
                    )
                )
            )
        }
    }
}
