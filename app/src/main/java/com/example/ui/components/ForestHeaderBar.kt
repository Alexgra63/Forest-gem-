package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ForestSurfaceVariant)
                    .border(1.dp, ForestCardBorder, CircleShape)
                    .testTag("pause_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Game",
                    tint = ForestEmeraldLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL ${levelConfig.levelNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForestGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = levelConfig.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = StarWhite,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Moves Remaining Badge
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(movesBadgeColor)
                    .border(2.dp, ForestGold, CircleShape)
                    .testTag("moves_counter"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$movesRemaining",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        lineHeight = 17.sp
                    )
                    Text(
                        text = "MOVES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
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

        // Bottom Row: Goals Badges (Coins & Moss)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Target Score Goal
            val scoreMet = score >= levelConfig.targetScore
            GoalPill(
                icon = "🎯",
                label = "Target",
                status = if (scoreMet) "Done!" else "$score / ${levelConfig.targetScore}",
                isComplete = scoreMet,
                modifier = Modifier.weight(1f)
            )

            // Coins Goal
            if (levelConfig.targetCoins > 0) {
                val coinsMet = coinsCollected >= levelConfig.targetCoins
                GoalPill(
                    icon = "🪙",
                    label = "Coins",
                    status = "$coinsCollected / ${levelConfig.targetCoins}",
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
                    status = "$mossCleared / ${levelConfig.targetMoss}",
                    isComplete = mossMet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GoalPill(
    icon: String,
    label: String,
    status: String,
    isComplete: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isComplete) ForestEmeraldDark.copy(alpha = 0.5f) else ForestSurfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isComplete) ForestEmeraldLight else ForestCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isComplete) ForestEmeraldLight else StarWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
