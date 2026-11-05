package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BoardTile
import com.example.model.ItemType
import com.example.model.SpecialType
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GemTileView(
    tile: BoardTile,
    isSelected: Boolean,
    isTargetMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for selected or special tiles
    val infiniteTransition = rememberInfiniteTransition(label = "TilePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "starRotation"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(1.5.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 24.dp),
                onClick = onClick
            )
            .testTag("tile_${tile.row}_${tile.col}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)
            val radius = w * 0.42f

            // 1. Draw Moss base layer if tile has moss
            if (tile.hasMoss) {
                drawMossLayer(center, radius)
            }

            // 2. Draw Selector / Target Ring
            if (isSelected) {
                drawCircle(
                    color = ForestGold.copy(alpha = 0.35f),
                    radius = radius * 1.15f
                )
                drawCircle(
                    color = ForestGold,
                    radius = radius * 1.15f,
                    style = Stroke(width = 3.dp.toPx())
                )
            } else if (isTargetMode) {
                drawCircle(
                    color = FairyPink.copy(alpha = 0.3f),
                    radius = radius * 1.15f
                )
                drawCircle(
                    color = FairyPink,
                    radius = radius * 1.15f,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 3. Draw Gem or Coin
            val currentScale = if (isSelected) pulseScale else 1f
            val itemRadius = radius * currentScale

            when (tile.type) {
                ItemType.GOLD_COIN -> drawGoldenCoin(center, itemRadius)
                ItemType.MOON_COIN -> drawSilverMoonCoin(center, itemRadius)
                ItemType.EMERALD -> drawEmeraldGem(center, itemRadius)
                ItemType.RUBY -> drawRubyGem(center, itemRadius)
                ItemType.SAPPHIRE -> drawSapphireGem(center, itemRadius)
                ItemType.AMETHYST -> drawAmethystGem(center, itemRadius)
                ItemType.TOPAZ -> drawTopazGem(center, itemRadius)
            }

            // 4. Draw Special overlay effects (Row ray, Col ray, Spore Bomb, Prismatic Star)
            when (tile.special) {
                SpecialType.ROW_BLAST -> drawRowBlastIndicator(center, itemRadius)
                SpecialType.COL_BLAST -> drawColBlastIndicator(center, itemRadius)
                SpecialType.SPORE_BOMB -> drawSporeBombIndicator(center, itemRadius, pulseScale)
                SpecialType.FOREST_STAR -> drawForestStarOverlay(center, itemRadius, starRotation)
                SpecialType.NONE -> { /* no-op */ }
            }
        }
    }
}

// --- Moss Underlay ---
private fun DrawScope.drawMossLayer(center: Offset, radius: Float) {
    val mossBrush = Brush.radialGradient(
        colors = listOf(Color(0xFF2D6A4F), Color(0xFF1B4332), Color(0xFF081C15)),
        center = center,
        radius = radius * 1.25f
    )
    drawRoundRect(
        brush = mossBrush,
        topLeft = Offset(center.x - radius * 1.15f, center.y - radius * 1.15f),
        size = Size(radius * 2.3f, radius * 2.3f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
    )
    // Tiny decorative leaf spores on the corner
    drawCircle(color = Color(0xFF52B788), radius = 2.5.dp.toPx(), center = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f))
    drawCircle(color = Color(0xFF74C69D), radius = 3.dp.toPx(), center = Offset(center.x + radius * 0.8f, center.y + radius * 0.6f))
    drawCircle(color = Color(0xFF40916C), radius = 2.dp.toPx(), center = Offset(center.x + radius * 0.5f, center.y - radius * 0.8f))
}

// --- Golden Tree Coin ---
private fun DrawScope.drawGoldenCoin(center: Offset, radius: Float) {
    // Outer drop shadow
    drawCircle(color = Color(0x66000000), radius = radius, center = center + Offset(1.5f, 2.5f))

    // Coin outer rim
    val rimBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFEE99), Color(0xFFFFD166), Color(0xFFB37400)),
        center = center - Offset(radius * 0.25f, radius * 0.25f),
        radius = radius
    )
    drawCircle(brush = rimBrush, radius = radius, center = center)

    // Inner rim
    drawCircle(
        color = Color(0xFF996300),
        radius = radius * 0.88f,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Coin face background
    val faceBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFDF70), Color(0xFFE5A919), Color(0xFFB87800)),
        start = Offset(center.x - radius, center.y - radius),
        end = Offset(center.x + radius, center.y + radius)
    )
    drawCircle(brush = faceBrush, radius = radius * 0.82f, center = center)

    // Celtic / Tree of Life Emblem
    val leafPath = Path().apply {
        // Central trunk / stem
        moveTo(center.x, center.y + radius * 0.45f)
        lineTo(center.x, center.y - radius * 0.35f)
        // Left branch
        moveTo(center.x, center.y + radius * 0.1f)
        quadraticTo(center.x - radius * 0.35f, center.y, center.x - radius * 0.4f, center.y - radius * 0.25f)
        // Right branch
        moveTo(center.x, center.y + radius * 0.1f)
        quadraticTo(center.x + radius * 0.35f, center.y, center.x + radius * 0.4f, center.y - radius * 0.25f)
    }
    drawPath(path = leafPath, color = Color(0xFFFFF9DB), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    // Central gem crown on coin
    drawCircle(color = ForestEmeraldLight, radius = radius * 0.18f, center = Offset(center.x, center.y - radius * 0.35f))
    // Specular shine
    drawCircle(color = Color(0xCCFFFFFF), radius = radius * 0.12f, center = Offset(center.x - radius * 0.4f, center.y - radius * 0.4f))
}

// --- Silver Moon Coin ---
private fun DrawScope.drawSilverMoonCoin(center: Offset, radius: Float) {
    drawCircle(color = Color(0x66000000), radius = radius, center = center + Offset(1.5f, 2.5f))

    val silverRim = Brush.radialGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1), Color(0xFF64748B)),
        center = center - Offset(radius * 0.2f, radius * 0.2f),
        radius = radius
    )
    drawCircle(brush = silverRim, radius = radius, center = center)

    val moonFace = Brush.linearGradient(
        colors = listOf(Color(0xFFF8FAFC), Color(0xFF94A3B8), Color(0xFF475569)),
        start = Offset(center.x - radius, center.y - radius),
        end = Offset(center.x + radius, center.y + radius)
    )
    drawCircle(brush = moonFace, radius = radius * 0.82f, center = center)

    // Crescent Moon Symbol
    val moonPath = Path().apply {
        moveTo(center.x, center.y - radius * 0.45f)
        cubicTo(
            center.x + radius * 0.45f, center.y - radius * 0.2f,
            center.x + radius * 0.45f, center.y + radius * 0.2f,
            center.x, center.y + radius * 0.45f
        )
        cubicTo(
            center.x + radius * 0.15f, center.y + radius * 0.2f,
            center.x + radius * 0.15f, center.y - radius * 0.2f,
            center.x, center.y - radius * 0.45f
        )
        close()
    }
    drawPath(path = moonPath, color = Color(0xFFE2E8F0))
    drawCircle(color = Color(0xFF90E0EF), radius = radius * 0.12f, center = Offset(center.x - radius * 0.2f, center.y))
}

// --- Emerald Gem (Octagon) ---
private fun DrawScope.drawEmeraldGem(center: Offset, radius: Float) {
    drawCircle(color = Color(0x55000000), radius = radius * 0.95f, center = center + Offset(1.5f, 2.5f))

    val cut = radius * 0.35f
    val r = radius * 0.95f
    val octPath = Path().apply {
        moveTo(center.x - r + cut, center.y - r)
        lineTo(center.x + r - cut, center.y - r)
        lineTo(center.x + r, center.y - r + cut)
        lineTo(center.x + r, center.y + r - cut)
        lineTo(center.x + r - cut, center.y + r)
        lineTo(center.x - r + cut, center.y + r)
        lineTo(center.x - r, center.y + r - cut)
        lineTo(center.x - r, center.y - r + cut)
        close()
    }

    val emeraldBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF80FFDB), Color(0xFF06D6A0), Color(0xFF0E7A68), Color(0xFF04473D)),
        start = Offset(center.x - r, center.y - r),
        end = Offset(center.x + r, center.y + r)
    )
    drawPath(path = octPath, brush = emeraldBrush)

    // Inner table facet
    val inCut = cut * 0.6f
    val inR = r * 0.62f
    val inPath = Path().apply {
        moveTo(center.x - inR + inCut, center.y - inR)
        lineTo(center.x + inR - inCut, center.y - inR)
        lineTo(center.x + inR, center.y - inR + inCut)
        lineTo(center.x + inR, center.y + inR - inCut)
        lineTo(center.x + inR - inCut, center.y + inR)
        lineTo(center.x - inR + inCut, center.y + inR)
        lineTo(center.x - inR, center.y + inR - inCut)
        lineTo(center.x - inR, center.y - inR + inCut)
        close()
    }
    drawPath(path = inPath, color = Color(0x33FFFFFF))
    drawPath(path = inPath, color = Color(0x66004B3D), style = Stroke(width = 1.dp.toPx()))

    // Star facet gleam
    drawCircle(color = Color(0xDDFFFFFF), radius = radius * 0.16f, center = Offset(center.x - inR * 0.5f, center.y - inR * 0.5f))
}

// --- Ruby Gem (Diamond Rhombus) ---
private fun DrawScope.drawRubyGem(center: Offset, radius: Float) {
    drawCircle(color = Color(0x55000000), radius = radius * 0.95f, center = center + Offset(1.5f, 2.5f))

    val rx = radius * 0.92f
    val ry = radius * 1.05f
    val rubyPath = Path().apply {
        moveTo(center.x, center.y - ry)
        lineTo(center.x + rx, center.y)
        lineTo(center.x, center.y + ry)
        lineTo(center.x - rx, center.y)
        close()
    }

    val rubyBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF758F), Color(0xFFFF0054), Color(0xFF9E0038), Color(0xFF4A0019)),
        center = center - Offset(rx * 0.3f, ry * 0.3f),
        radius = radius * 1.1f
    )
    drawPath(path = rubyPath, brush = rubyBrush)

    // Facet lines
    val innerPath = Path().apply {
        moveTo(center.x, center.y - ry * 0.55f)
        lineTo(center.x + rx * 0.55f, center.y)
        lineTo(center.x, center.y + ry * 0.55f)
        lineTo(center.x - rx * 0.55f, center.y)
        close()
    }
    drawPath(path = innerPath, color = Color(0x33FFFFFF))
    drawCircle(color = Color(0xEEFFFFFF), radius = radius * 0.15f, center = Offset(center.x - rx * 0.3f, center.y - ry * 0.3f))
}

// --- Sapphire Gem (Teardrop / Water Jewel) ---
private fun DrawScope.drawSapphireGem(center: Offset, radius: Float) {
    drawCircle(color = Color(0x55000000), radius = radius * 0.95f, center = center + Offset(1.5f, 2.5f))

    val tearPath = Path().apply {
        moveTo(center.x, center.y - radius * 1.05f)
        cubicTo(
            center.x + radius * 0.95f, center.y - radius * 0.1f,
            center.x + radius * 0.95f, center.y + radius * 0.85f,
            center.x, center.y + radius * 0.85f
        )
        cubicTo(
            center.x - radius * 0.95f, center.y + radius * 0.85f,
            center.x - radius * 0.95f, center.y - radius * 0.1f,
            center.x, center.y - radius * 1.05f
        )
        close()
    }

    val sapphireBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF90E0EF), Color(0xFF00B4D8), Color(0xFF0077B6), Color(0xFF03045E)),
        start = Offset(center.x - radius, center.y - radius),
        end = Offset(center.x + radius, center.y + radius)
    )
    drawPath(path = tearPath, brush = sapphireBrush)

    // Highlight teardrop
    drawCircle(color = Color(0xDDFFFFFF), radius = radius * 0.16f, center = Offset(center.x - radius * 0.3f, center.y - radius * 0.2f))
    drawCircle(color = Color(0x99FFFFFF), radius = radius * 0.09f, center = Offset(center.x + radius * 0.25f, center.y + radius * 0.4f))
}

// --- Amethyst Gem (Hexagonal Shard) ---
private fun DrawScope.drawAmethystGem(center: Offset, radius: Float) {
    drawCircle(color = Color(0x55000000), radius = radius * 0.95f, center = center + Offset(1.5f, 2.5f))

    val hexPath = Path()
    for (i in 0 until 6) {
        val angle = (i * 60 - 30) * PI / 180.0
        val x = (center.x + radius * 0.95f * cos(angle)).toFloat()
        val y = (center.y + radius * 0.95f * sin(angle)).toFloat()
        if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
    }
    hexPath.close()

    val amethystBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFE0AAFF), Color(0xFFC77DFF), Color(0xFF7B2CBF), Color(0xFF240046)),
        center = center - Offset(radius * 0.25f, radius * 0.25f),
        radius = radius
    )
    drawPath(path = hexPath, brush = amethystBrush)

    // Inner facet
    val inHex = Path()
    for (i in 0 until 6) {
        val angle = (i * 60 - 30) * PI / 180.0
        val x = (center.x + radius * 0.55f * cos(angle)).toFloat()
        val y = (center.y + radius * 0.55f * sin(angle)).toFloat()
        if (i == 0) inHex.moveTo(x, y) else inHex.lineTo(x, y)
    }
    inHex.close()
    drawPath(path = inHex, color = Color(0x44FFFFFF))
    drawCircle(color = Color(0xEEFFFFFF), radius = radius * 0.15f, center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f))
}

// --- Topaz Amber (Sun Marquise) ---
private fun DrawScope.drawTopazGem(center: Offset, radius: Float) {
    drawCircle(color = Color(0x55000000), radius = radius * 0.95f, center = center + Offset(1.5f, 2.5f))

    val marquisePath = Path().apply {
        moveTo(center.x - radius * 0.95f, center.y)
        quadraticTo(center.x, center.y - radius * 0.95f, center.x + radius * 0.95f, center.y)
        quadraticTo(center.x, center.y + radius * 0.95f, center.x - radius * 0.95f, center.y)
        close()
    }

    val topazBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFEE99), Color(0xFFFFB703), Color(0xFFFB8500), Color(0xFF944400)),
        start = Offset(center.x - radius, center.y - radius),
        end = Offset(center.x + radius, center.y + radius)
    )
    drawPath(path = marquisePath, brush = topazBrush)
    drawCircle(color = Color(0xEEFFFFFF), radius = radius * 0.15f, center = Offset(center.x - radius * 0.3f, center.y - radius * 0.2f))
}

// --- Special Overlays ---
private fun DrawScope.drawRowBlastIndicator(center: Offset, radius: Float) {
    // Left & Right radiant energy arrows
    val arrowBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFE066), Color(0xFFFFAA00))
    )
    drawLine(
        brush = arrowBrush,
        start = Offset(center.x - radius * 0.9f, center.y),
        end = Offset(center.x + radius * 0.9f, center.y),
        strokeWidth = 3.5.dp.toPx(),
        cap = StrokeCap.Round
    )
    // Left arrowhead
    val leftArrow = Path().apply {
        moveTo(center.x - radius * 0.9f, center.y)
        lineTo(center.x - radius * 0.55f, center.y - radius * 0.28f)
        lineTo(center.x - radius * 0.55f, center.y + radius * 0.28f)
        close()
    }
    drawPath(path = leftArrow, color = Color.White)
    // Right arrowhead
    val rightArrow = Path().apply {
        moveTo(center.x + radius * 0.9f, center.y)
        lineTo(center.x + radius * 0.55f, center.y - radius * 0.28f)
        lineTo(center.x + radius * 0.55f, center.y + radius * 0.28f)
        close()
    }
    drawPath(path = rightArrow, color = Color.White)
}

private fun DrawScope.drawColBlastIndicator(center: Offset, radius: Float) {
    // Up & Down radiant energy arrows
    val arrowBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFE066), Color(0xFFFFAA00))
    )
    drawLine(
        brush = arrowBrush,
        start = Offset(center.x, center.y - radius * 0.9f),
        end = Offset(center.x, center.y + radius * 0.9f),
        strokeWidth = 3.5.dp.toPx(),
        cap = StrokeCap.Round
    )
    // Top arrowhead
    val topArrow = Path().apply {
        moveTo(center.x, center.y - radius * 0.9f)
        lineTo(center.x - radius * 0.28f, center.y - radius * 0.55f)
        lineTo(center.x + radius * 0.28f, center.y - radius * 0.55f)
        close()
    }
    drawPath(path = topArrow, color = Color.White)
    // Bottom arrowhead
    val btmArrow = Path().apply {
        moveTo(center.x, center.y + radius * 0.9f)
        lineTo(center.x - radius * 0.28f, center.y + radius * 0.55f)
        lineTo(center.x + radius * 0.28f, center.y + radius * 0.55f)
        close()
    }
    drawPath(path = btmArrow, color = Color.White)
}

private fun DrawScope.drawSporeBombIndicator(center: Offset, radius: Float, pulse: Float) {
    // Exploding magic spore aura with 4 swirling satellite spores
    val sporeRingRadius = radius * 0.75f * pulse
    drawCircle(
        color = FairyPink.copy(alpha = 0.5f),
        radius = sporeRingRadius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    // 4 satellite spore lights
    val sporeAngles = listOf(45, 135, 225, 315)
    for (a in sporeAngles) {
        val rad = a * PI / 180.0
        val sx = (center.x + sporeRingRadius * cos(rad)).toFloat()
        val sy = (center.y + sporeRingRadius * sin(rad)).toFloat()
        drawCircle(color = Color(0xFFFF70A6), radius = 3.dp.toPx(), center = Offset(sx, sy))
        drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(sx, sy))
    }
}

private fun DrawScope.drawForestStarOverlay(center: Offset, radius: Float, rotationDegrees: Float) {
    // Rainbow Prismatic Tree Star Orb!
    val rainbowBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFFFF3366),
            Color(0xFFFFD166),
            Color(0xFF06D6A0),
            Color(0xFF00B4D8),
            Color(0xFFB5179E),
            Color(0xFFFF3366)
        ),
        center = center
    )
    // Glowing rainbow halo
    drawCircle(brush = rainbowBrush, radius = radius * 0.92f, center = center)
    // Inner crystal core
    drawCircle(color = Color(0xEEFFFFFF), radius = radius * 0.55f, center = center)

    // 8-pointed star in the center
    val starPath = Path()
    val points = 8
    val innerR = radius * 0.28f
    val outerR = radius * 0.52f
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = (i * (360.0 / (points * 2)) + rotationDegrees) * PI / 180.0
        val x = (center.x + r * cos(angle)).toFloat()
        val y = (center.y + r * sin(angle)).toFloat()
        if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
    }
    starPath.close()
    drawPath(path = starPath, color = ForestGold)
    drawCircle(color = Color.White, radius = radius * 0.18f, center = center)
}
