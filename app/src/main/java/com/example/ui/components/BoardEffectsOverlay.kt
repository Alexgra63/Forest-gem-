package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.engine.Match3Engine
import com.example.model.BoardVisualEffect
import com.example.ui.theme.ForestGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BoardEffectsOverlay(
    effects: List<BoardVisualEffect>,
    onEffectFinished: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        for (effect in effects) {
            when (effect) {
                is BoardVisualEffect.RowSliderFlash -> {
                    RowSliderFlashView(
                        effect = effect,
                        onFinished = { onEffectFinished(effect.id) }
                    )
                }
                is BoardVisualEffect.ColSliderFlash -> {
                    ColSliderFlashView(
                        effect = effect,
                        onFinished = { onEffectFinished(effect.id) }
                    )
                }
                is BoardVisualEffect.ChainStackLightBurst -> {
                    ChainStackLightBurstView(
                        effect = effect,
                        onFinished = { onEffectFinished(effect.id) }
                    )
                }
                is BoardVisualEffect.FullCanvasTreasureWipe -> {
                    FullCanvasTreasureWipeView(
                        effect = effect,
                        onFinished = { onEffectFinished(effect.id) }
                    )
                }
            }
        }
    }
}

/**
 * 1. White flash beam that wipes the row with a swoosh.
 */
@Composable
private fun RowSliderFlashView(
    effect: BoardVisualEffect.RowSliderFlash,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(effect.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = effect.durationMs, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellH = h / Match3Engine.ROWS
        val cy = (effect.row + 0.5f) * cellH

        val p = progress.value
        val alpha = (1f - p).coerceIn(0f, 1f)

        // Outer radiant light aura across the row
        val haloHeight = cellH * (1.2f + p * 0.8f)
        val haloBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x99FFFFFF).copy(alpha = alpha * 0.6f),
                Color.White.copy(alpha = alpha * 0.95f),
                Color(0x99FFFFFF).copy(alpha = alpha * 0.6f),
                Color.Transparent
            ),
            startY = cy - haloHeight / 2f,
            endY = cy + haloHeight / 2f
        )
        drawRect(
            brush = haloBrush,
            topLeft = Offset(0f, cy - haloHeight / 2f),
            size = Size(w, haloHeight)
        )

        // Intense laser white core
        val beamThickness = (cellH * 0.35f * (1f - p * 0.6f)).coerceAtLeast(2.dp.toPx())
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(0f, cy),
            end = Offset(w, cy),
            strokeWidth = beamThickness,
            cap = StrokeCap.Round
        )

        // Golden & cyan prismatic edge streaks
        drawLine(
            color = Color(0xDD80EEFF).copy(alpha = alpha * 0.85f),
            start = Offset(0f, cy - beamThickness * 0.5f),
            end = Offset(w, cy - beamThickness * 0.5f),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color(0xFFFFE066).copy(alpha = alpha * 0.85f),
            start = Offset(0f, cy + beamThickness * 0.5f),
            end = Offset(w, cy + beamThickness * 0.5f),
            strokeWidth = 2.dp.toPx()
        )

        // Radiant traveling sparkle particles along the row
        val numSparkles = 10
        for (i in 0 until numSparkles) {
            val sparkX = ((i + 0.5f) / numSparkles) * w
            val sparkRadius = (4.dp.toPx() * (1f - p) * (if (i % 2 == 0) 1.4f else 0.8f))
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = sparkRadius,
                center = Offset(sparkX, cy)
            )
        }
    }
}

/**
 * 2. White flash beam that wipes the column with a swoosh.
 */
@Composable
private fun ColSliderFlashView(
    effect: BoardVisualEffect.ColSliderFlash,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(effect.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = effect.durationMs, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / Match3Engine.COLS
        val cx = (effect.col + 0.5f) * cellW

        val p = progress.value
        val alpha = (1f - p).coerceIn(0f, 1f)

        // Outer radiant light aura across the column
        val haloWidth = cellW * (1.2f + p * 0.8f)
        val haloBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x99FFFFFF).copy(alpha = alpha * 0.6f),
                Color.White.copy(alpha = alpha * 0.95f),
                Color(0x99FFFFFF).copy(alpha = alpha * 0.6f),
                Color.Transparent
            ),
            startX = cx - haloWidth / 2f,
            endX = cx + haloWidth / 2f
        )
        drawRect(
            brush = haloBrush,
            topLeft = Offset(cx - haloWidth / 2f, 0f),
            size = Size(haloWidth, h)
        )

        // Intense laser white core
        val beamThickness = (cellW * 0.35f * (1f - p * 0.6f)).coerceAtLeast(2.dp.toPx())
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(cx, 0f),
            end = Offset(cx, h),
            strokeWidth = beamThickness,
            cap = StrokeCap.Round
        )

        // Radiant edges
        drawLine(
            color = Color(0xDD80EEFF).copy(alpha = alpha * 0.85f),
            start = Offset(cx - beamThickness * 0.5f, 0f),
            end = Offset(cx - beamThickness * 0.5f, h),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color(0xFFFFE066).copy(alpha = alpha * 0.85f),
            start = Offset(cx + beamThickness * 0.5f, 0f),
            end = Offset(cx + beamThickness * 0.5f, h),
            strokeWidth = 2.dp.toPx()
        )

        // Traveling sparkles
        val numSparkles = 10
        for (i in 0 until numSparkles) {
            val sparkY = ((i + 0.5f) / numSparkles) * h
            val sparkRadius = (4.dp.toPx() * (1f - p) * (if (i % 2 == 0) 1.4f else 0.8f))
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = sparkRadius,
                center = Offset(cx, sparkY)
            )
        }
    }
}

/**
 * 3. Light spreading to each side removing gems/coins with coin burst SFX.
 */
@Composable
private fun ChainStackLightBurstView(
    effect: BoardVisualEffect.ChainStackLightBurst,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(effect.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = effect.durationMs, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / Match3Engine.COLS
        val cellH = h / Match3Engine.ROWS

        val cx = (effect.col + 0.5f) * cellW
        val cy = (effect.row + 0.5f) * cellH

        val p = progress.value
        val alpha = (1f - p).coerceIn(0f, 1f)

        // Colors based on whether it is a Coin Stack or Gem Chain
        val mainColor = if (effect.isCoinStack) ForestGold else Color(0xFFC77DFF)
        val glowColor = if (effect.isCoinStack) Color(0xFFFFEE99) else Color(0xFF80FFDB)

        // Light spreading outward to each of the 4 sides (Left, Right, Top, Bottom)
        val spreadDist = (cellW * 2.3f * p).coerceAtLeast(cellW * 0.4f)
        val beamWidth = (cellW * 0.45f * (1f - p * 0.7f)).coerceAtLeast(3.dp.toPx())

        // 4 Cardinal light beams spreading outward
        val directions = listOf(
            Offset(-spreadDist, 0f), // Left
            Offset(spreadDist, 0f),  // Right
            Offset(0f, -spreadDist), // Up
            Offset(0f, spreadDist)   // Down
        )

        directions.forEach { dir ->
            val endPoint = Offset(cx + dir.x, cy + dir.y)

            // Outer feathered glow
            drawLine(
                color = mainColor.copy(alpha = alpha * 0.6f),
                start = Offset(cx, cy),
                end = endPoint,
                strokeWidth = beamWidth * 2.2f,
                cap = StrokeCap.Round
            )
            // Core white light ray
            drawLine(
                color = Color.White.copy(alpha = alpha * 0.95f),
                start = Offset(cx, cy),
                end = endPoint,
                strokeWidth = beamWidth,
                cap = StrokeCap.Round
            )

            // Expanding light bloom circle at the tip of each side wiping gems/coins
            drawCircle(
                color = glowColor.copy(alpha = alpha * 0.5f),
                radius = cellW * 0.5f * (0.4f + p * 0.6f),
                center = endPoint
            )
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.85f),
                radius = cellW * 0.25f * (1f - p * 0.5f),
                center = endPoint
            )
        }

        // Center expanding coin shockwave ring
        val ringRadius = (cellW * 1.8f * p).coerceAtLeast(10f)
        drawCircle(
            color = mainColor.copy(alpha = alpha * 0.7f),
            radius = ringRadius,
            center = Offset(cx, cy),
            style = Stroke(width = (4.dp.toPx() * (1f - p)).coerceAtLeast(1.dp.toPx()))
        )

        // Center radiant core
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = cellW * 0.4f * (1f - p * 0.5f),
            center = Offset(cx, cy)
        )

        // Bursting coin / gem spark particles (16 radial sparks)
        val sparkCount = 16
        for (i in 0 until sparkCount) {
            val angle = (i * (360.0 / sparkCount) + (p * 45.0)) * PI / 180.0
            val sparkDist = cellW * 1.6f * p
            val sx = (cx + sparkDist * cos(angle)).toFloat()
            val sy = (cy + sparkDist * sin(angle)).toFloat()
            val sRadius = (3.5.dp.toPx() * (1f - p))

            drawCircle(
                color = if (i % 2 == 0) mainColor.copy(alpha = alpha) else Color.White.copy(alpha = alpha),
                radius = sRadius,
                center = Offset(sx, sy)
            )
        }
    }
}

/**
 * 4. Enchanted Treasure Chest pop: light spreads across the whole Canvas and wipes it all!
 */
@Composable
private fun FullCanvasTreasureWipeView(
    effect: BoardVisualEffect.FullCanvasTreasureWipe,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(effect.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = effect.durationMs, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / Match3Engine.COLS
        val cellH = h / Match3Engine.ROWS

        val cx = (effect.originCol + 0.5f) * cellW
        val cy = (effect.originRow + 0.5f) * cellH

        val p = progress.value
        val maxDim = kotlin.math.max(w, h) * 1.5f

        // Phase 1 (0..0.45): Golden rays shoot out from treasure chest
        val rayAlpha = if (p < 0.5f) (p * 2f).coerceIn(0f, 1f) else ((1f - p) * 2f).coerceIn(0f, 1f)
        val numRays = 16
        for (i in 0 until numRays) {
            val angle = (i * (360.0 / numRays) + (p * 60.0)) * PI / 180.0
            val rayLen = maxDim * (0.2f + p * 0.8f)
            val rx = (cx + rayLen * cos(angle)).toFloat()
            val ry = (cy + rayLen * sin(angle)).toFloat()

            drawLine(
                color = ForestGold.copy(alpha = rayAlpha * 0.5f),
                start = Offset(cx, cy),
                end = Offset(rx, ry),
                strokeWidth = 5.dp.toPx() * (1f - p * 0.5f),
                cap = StrokeCap.Round
            )
        }

        // Phase 2: Giant expanding divine white & gold shockwave covering entire canvas
        val waveRadius = maxDim * p
        drawCircle(
            color = ForestGold.copy(alpha = (1f - p).coerceIn(0f, 1f) * 0.75f),
            radius = waveRadius,
            center = Offset(cx, cy),
            style = Stroke(width = (24.dp.toPx() * (1f - p * 0.8f)).coerceAtLeast(2.dp.toPx()))
        )

        // Phase 3: Full Canvas blinding wipe flash (washes over entire board)
        val flashAlpha = when {
            p < 0.25f -> (p / 0.25f) * 0.95f
            p < 0.65f -> 0.95f
            else -> ((1f - p) / 0.35f) * 0.95f
        }.coerceIn(0f, 1f)

        // Full canvas white & golden wash
        drawRect(
            color = Color.White.copy(alpha = flashAlpha),
            topLeft = Offset.Zero,
            size = Size(w, h)
        )
        drawRect(
            color = ForestGold.copy(alpha = flashAlpha * 0.45f),
            topLeft = Offset.Zero,
            size = Size(w, h)
        )

        // Glittering stardust particles across the entire board canvas
        val numStardust = 32
        for (i in 0 until numStardust) {
            val randX = ((i * 37) % 100) / 100f * w
            val randY = ((i * 59) % 100) / 100f * h
            val particleAlpha = (flashAlpha * (0.4f + 0.6f * sin((p * 10f + i).toDouble()).toFloat())).coerceIn(0f, 1f)
            drawCircle(
                color = if (i % 2 == 0) Color.White.copy(alpha = particleAlpha) else ForestGold.copy(alpha = particleAlpha),
                radius = (3.dp.toPx() + (i % 3).dp.toPx()),
                center = Offset(randX, randY)
            )
        }
    }
}
