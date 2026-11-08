package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ForestGold
import com.example.ui.theme.TextGold

/**
 * Custom 3D wooden plank button styled with gold trim and vine leaf accents
 * using the provided button_background asset (btn_wood_rounded).
 */
@Composable
fun WoodPlankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconDrawable: Int? = null,
    textColor: Color = Color(0xFFFFF6D8),
    fontSize: TextUnit = 16.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "wood_button_scale"
    )

    val isCompact = fontSize <= 13.sp
    val iconSize = if (isCompact) 16.dp else 20.dp
    val iconSpacing = if (isCompact) 5.dp else 8.dp
    val textSpacing = if (isCompact) 0.5.sp else 1.2.sp

    Box(
        modifier = modifier
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = ForestGold)
            ) { onClick() }
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Wood Plank Button Background (2141 x 642 PNG with gold border and leaf corners)
        Image(
            painter = painterResource(id = R.drawable.btn_wood_rounded),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Label and Icon Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextGold,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(iconSpacing))
            } else if (iconDrawable != null) {
                Image(
                    painter = painterResource(id = iconDrawable),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(iconSpacing))
            }

            Text(
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = textSpacing,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF1E0F04),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}

/**
 * Custom 3D wooden sound/vibrate toggle button with ornate circular badge and wooden pill,
 * displaying ON or OFF in the carved wooden area.
 */
@Composable
fun WoodToggleOptionButton(
    imageRes: Int,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "wood_toggle_scale"
    )

    Box(
        modifier = modifier
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = ForestGold)
            ) { onToggle() }
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Wooden base image (sound or haptic)
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .then(if (!enabled) Modifier.alpha(0.6f) else Modifier),
            contentScale = ContentScale.Fit
        )

        // Text "ON" or "OFF" positioned over the right wooden pill section
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (enabled) "ON" else "OFF",
                    color = if (enabled) Color(0xFFFFF7D6) else Color(0xFFD67272),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF1F0F02),
                            offset = Offset(2f, 2f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }
    }
}

/**
 * Custom 3D circular wooden plaque button framed with woven vines, moss, and leaves
 * using circle.png. Animates scale down smoothly (0.90f) when clicked/pressed.
 */
@Composable
fun WoodCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "wood_circle_scale"
    )

    Box(
        modifier = modifier
            .scale(animatedScale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = ForestGold),
                enabled = enabled,
                onClick = onClick
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

/**
 * Custom 3D circular wooden plaque badge for non-interactive displays (e.g. moves remaining, level indicator).
 */
@Composable
fun WoodCircleBadge(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

