package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGold
import com.example.ui.theme.ForestEmeraldLight
import kotlin.random.Random

data class FloatingPraise(
    val id: Long,
    val text: String,
    val color: Color,
    val startX: Float,
    val startY: Float
)

@Composable
fun ForestFireflies(modifier: Modifier = Modifier) {
    val fireflies = remember {
        List(24) {
            FireflyState(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 2f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                color = if (Random.nextBoolean()) ForestEmeraldLight else ForestGold
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "FirefliesAnim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        fireflies.forEachIndexed { i, f ->
            val angle = (phase * 2 * Math.PI * f.speed + i).toFloat()
            val dx = kotlin.math.cos(angle) * 30f
            val dy = kotlin.math.sin(angle) * 40f
            val px = ((f.x * w) + dx) % w
            val py = ((f.y * h) + dy) % h

            val alpha = (0.3f + 0.6f * kotlin.math.abs(kotlin.math.sin(angle * 2f))).coerceIn(0f, 1f)
            // Firefly glow aura
            drawCircle(
                color = f.color.copy(alpha = alpha * 0.4f),
                radius = f.size * 2.8f,
                center = Offset(px, py)
            )
            // Core
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = f.size,
                center = Offset(px, py)
            )
        }
    }
}

private class FireflyState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun FloatingPraiseOverlay(
    praiseText: String?,
    modifier: Modifier = Modifier
) {
    if (praiseText.isNullOrBlank()) return

    var visible by remember(praiseText) { mutableStateOf(true) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(praiseText) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
        visible = false
    }

    if (visible) {
        val alpha = (1f - animProgress.value).coerceIn(0f, 1f)
        val yOffset = (-animProgress.value * 60).dp
        val scale = 1f + (animProgress.value * 0.35f)

        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = praiseText,
                style = TextStyle(
                    fontSize = (26 * scale).sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGold.copy(alpha = alpha),
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = alpha * 0.9f),
                        offset = Offset(3f, 4f),
                        blurRadius = 8f
                    )
                ),
                modifier = Modifier.offset { IntOffset(0, yOffset.roundToPx()) }
            )
        }
    }
}
