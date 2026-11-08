package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "powerup_press_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "SelectedPowerUp")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val totalScale = pulseScale * pressScale

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .scale(totalScale)
                .size(50.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = ForestGold),
                    onClick = onClick
                )
                .testTag("powerup_${powerUp.id}"),
            contentAlignment = Alignment.Center
        ) {
            // Cut-out circular wooden plaque with braided vines and moss
            Image(
                painter = painterResource(id = R.drawable.circle),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Golden glowing border overlay when active/selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.5.dp, ForestGold, CircleShape)
                )
            }

            Text(
                text = powerUp.iconSymbol,
                fontSize = 22.sp
            )

            // Count badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (count > 0) ForestGold else Color(0xFF333333))
                    .border(1.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 0) "$count" else "+",
                    color = if (count > 0) Color.Black else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = powerUp.title.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) ForestGold else TextMuted,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
