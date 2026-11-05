package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.PowerUpType
import com.example.ui.theme.*

@Composable
fun PowerUpBar(
    powerUpCounts: Map<PowerUpType, Int>,
    activePowerUp: PowerUpType?,
    onPowerUpSelected: (PowerUpType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(
                1.dp,
                ForestCardBorder.copy(alpha = 0.7f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        color = ForestDarkBackground.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (activePowerUp != null) {
                Text(
                    text = "Tap a tile to use ${activePowerUp.title}! (Tap icon to cancel)",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForestGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PowerUpType.entries.forEach { powerUp ->
                    val count = powerUpCounts[powerUp] ?: 0
                    val isSelected = activePowerUp == powerUp
                    PowerUpButton(
                        powerUp = powerUp,
                        count = count,
                        isSelected = isSelected,
                        onClick = { onPowerUpSelected(powerUp) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PowerUpButton(
    powerUp: PowerUpType,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SelectedPowerUp")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> ForestGold
            count > 0 -> ForestEmerald
            else -> ForestCardBorder.copy(alpha = 0.4f)
        },
        label = "borderColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isSelected) {
                            listOf(ForestSurfaceVariant, ForestEmeraldDark)
                        } else {
                            listOf(ForestSurface, ForestSurfaceVariant)
                        }
                    )
                )
                .border(2.dp, borderColor, CircleShape)
                .testTag("powerup_${powerUp.id}"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = powerUp.iconSymbol,
                fontSize = 20.sp
            )

            // Count badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(if (count > 0) ForestGold else Color.Gray)
                    .border(1.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 0) "$count" else "+",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = powerUp.title.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) ForestGold else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
