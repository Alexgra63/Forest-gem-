package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Biome
import com.example.model.LevelConfig
import com.example.model.LevelRecord
import com.example.model.LevelRepository
import com.example.ui.components.ForestFireflies
import com.example.ui.components.WoodCircleButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun LevelMapScreen(
    levelRecords: List<LevelRecord>,
    playerCoins: Int,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Find highest unlocked level
    val highestUnlockedLevel = remember(levelRecords) {
        levelRecords.filter { it.isUnlocked }.maxOfOrNull { it.levelNumber } ?: 1
    }

    // Number of levels to display along the climbing grapevine (always keeps 10 ahead of player)
    val totalLevelsToShow = remember(highestUnlockedLevel) {
        (highestUnlockedLevel + 10).coerceAtLeast(18)
    }

    val levelsList = remember(totalLevelsToShow) {
        LevelRepository.getLevelsUpTo(totalLevelsToShow)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll automatically to current highest unlocked level on start
    LaunchedEffect(highestUnlockedLevel) {
        val targetIndex = (highestUnlockedLevel - 1).coerceIn(0, levelsList.size - 1)
        listState.scrollToItem(targetIndex)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Enchanted deep forest backdrop
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
                            ForestDarkBackground.copy(alpha = 0.92f),
                            ForestDarkBackground.copy(alpha = 0.75f),
                            ForestDarkBackground.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // Floating ambient forest fireflies
        ForestFireflies()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WoodCircleButton(
                    onClick = onBack,
                    modifier = Modifier.size(44.dp),
                    testTag = "map_back_button"
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Menu",
                        tint = ForestGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "THE ENDLESS VINE",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForestGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "Climbing Level $highestUnlockedLevel • Skyward Path",
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestEmeraldLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ForestSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_forest_gold_coin),
                            contentDescription = "Coins",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$playerCoins",
                            style = MaterialTheme.typography.labelLarge,
                            color = ForestGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Endless Grapevine Scroll (reverseLayout = true so Level 1 is at bottom and higher levels go UPWARDS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 80.dp, bottom = 40.dp)
                ) {
                    // Item 0 is Level 1 at bottom, Item N-1 is Level N at top
                    itemsIndexed(levelsList) { index, levelConfig ->
                        val record = levelRecords.find { it.levelNumber == levelConfig.levelNumber }
                        val isUnlocked = record?.isUnlocked ?: (levelConfig.levelNumber == 1)
                        val stars = record?.stars ?: 0
                        val isCurrentHighest = levelConfig.levelNumber == highestUnlockedLevel

                        GrapeStemLevelRow(
                            levelConfig = levelConfig,
                            isUnlocked = isUnlocked,
                            stars = stars,
                            isCurrentHighest = isCurrentHighest,
                            index = index,
                            totalCount = levelsList.size,
                            onClick = {
                                if (isUnlocked) onLevelSelected(levelConfig.levelNumber)
                            }
                        )
                    }

                    // Top Canopy continuation item
                    item {
                        CelestialCanopyHeader()
                    }
                }

                // Jump to Current Level Floating Button
                WoodCircleButton(
                    onClick = {
                        coroutineScope.launch {
                            val targetIndex = (highestUnlockedLevel - 1).coerceIn(0, levelsList.size - 1)
                            listState.animateScrollToItem(targetIndex)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(50.dp),
                    testTag = "jump_to_current_level_button"
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Jump to active level",
                        tint = ForestGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * A single winding segment of the endless grapevine stem with grapes, leaves, and a jewel level node.
 */
@Composable
private fun GrapeStemLevelRow(
    levelConfig: LevelConfig,
    isUnlocked: Boolean,
    stars: Int,
    isCurrentHighest: Boolean,
    index: Int,
    totalCount: Int,
    onClick: () -> Unit
) {
    // Serpentine horizontal position of the grape node along the stem
    // Alternates in an organic S-curve between 25% and 75% screen width
    val normalizedOffset = 0.5f + (0.28f * sin(index * 1.05f))
    val isLeftSided = normalizedOffset < 0.5f

    // Pulse animation for the current active level node
    val infiniteTransition = rememberInfiniteTransition(label = "ActiveLevelPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrentHighest) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .testTag("grape_level_${levelConfig.levelNumber}")
    ) {
        // 1. Draw Winding Green Grape Stem with Leaves & Tendrils
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val currX = w * normalizedOffset
            val currY = h * 0.5f

            val prevNormalized = if (index > 0) 0.5f + (0.28f * sin((index - 1) * 1.05f)) else normalizedOffset
            val prevX = w * prevNormalized
            val prevY = h

            val nextNormalized = if (index < totalCount - 1) 0.5f + (0.28f * sin((index + 1) * 1.05f)) else normalizedOffset
            val nextX = w * nextNormalized
            val nextY = 0f

            // A. Draw main thick woody green stem path
            val stemPath = Path().apply {
                moveTo(prevX, prevY)
                cubicTo(
                    prevX, (prevY + currY) / 2f,
                    currX, (prevY + currY) / 2f,
                    currX, currY
                )
                cubicTo(
                    currX, (currY + nextY) / 2f,
                    nextX, (currY + nextY) / 2f,
                    nextX, nextY
                )
            }

            // Stem outer bark shadow
            drawPath(
                path = stemPath,
                color = Color(0xFF0A1A12),
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            // Stem woody green core with vine texture
            val stemBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1B4332),
                    Color(0xFF2D6A4F),
                    Color(0xFF52B788),
                    Color(0xFF1B4332)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
            drawPath(
                path = stemPath,
                brush = stemBrush,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Inner golden vine highlight line
            drawPath(
                path = stemPath,
                color = ForestGold.copy(alpha = 0.35f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // B. Draw Grapevine Leaves & Curly Tendrils sprouting from stem
            drawGrapeVineDecorations(currX, currY, isLeftSided)
        }

        // 2. The Interactive Grape Jewel Level Node & Parchment Info
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = maxWidth
            val nodeX = w * normalizedOffset

            // Positioning Node
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isLeftSided) Arrangement.Start else Arrangement.End
            ) {
                if (!isLeftSided) {
                    // Level info plaque on left if node is on right
                    LevelInfoPlaque(
                        levelConfig = levelConfig,
                        isUnlocked = isUnlocked,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    )
                }

                // The Grape Orb Node
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    if (isCurrentHighest) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGold,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Text(
                                text = "PLAY ▶",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }

                    val nodeInteractionSource = remember { MutableInteractionSource() }
                    val isNodePressed by nodeInteractionSource.collectIsPressedAsState()
                    val nodePressScale by animateFloatAsState(
                        targetValue = if (isNodePressed && isUnlocked) 0.90f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                        label = "node_press_scale"
                    )

                    // Circular Wood Vine Level Orb Plaque (circle.png)
                    Box(
                        modifier = Modifier
                            .scale(pulseScale * nodePressScale)
                            .size(60.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = nodeInteractionSource,
                                indication = ripple(bounded = true, color = ForestGold),
                                enabled = isUnlocked,
                                onClick = onClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Level ${levelConfig.levelNumber}",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (!isUnlocked) Modifier.alpha(0.55f) else Modifier)
                        )

                        if (isCurrentHighest) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.5.dp, ForestGold, CircleShape)
                            )
                        }

                        if (isUnlocked) {
                            Text(
                                text = "${levelConfig.levelNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    shadow = Shadow(
                                        color = Color(0xFF1E0E03),
                                        offset = Offset(1.5f, 2f),
                                        blurRadius = 3f
                                    )
                                ),
                                color = if (isCurrentHighest) ForestGold else Color(0xFFFFF6D8),
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFFCCCCCC).copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 3 Stars Underneath
                    Row(
                        modifier = Modifier.padding(top = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        for (s in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (s <= stars) ForestGold else Color.Gray.copy(alpha = 0.35f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                if (isLeftSided) {
                    // Level info plaque on right if node is on left
                    LevelInfoPlaque(
                        levelConfig = levelConfig,
                        isUnlocked = isUnlocked,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Draws organic grape leaves and curly tendrils on the stem.
 */
private fun DrawScope.drawGrapeVineDecorations(cx: Float, cy: Float, isLeftSided: Boolean) {
    val leafColor = Color(0xFF40916C)
    val leafHighlight = Color(0xFF74C69D)
    val grapeColor = Color(0xFF7B2CBF)
    val grapeHighlight = Color(0xFFE0AAFF)

    val dir = if (isLeftSided) 1f else -1f

    // 1. Grape leaf beside the stem
    val leafCenter = Offset(cx - (dir * 36.dp.toPx()), cy - 14.dp.toPx())
    val leafPath = Path().apply {
        moveTo(leafCenter.x, leafCenter.y - 12.dp.toPx())
        quadraticTo(leafCenter.x + (dir * 18.dp.toPx()), leafCenter.y, leafCenter.x, leafCenter.y + 12.dp.toPx())
        quadraticTo(leafCenter.x - (dir * 18.dp.toPx()), leafCenter.y, leafCenter.x, leafCenter.y - 12.dp.toPx())
    }
    drawPath(path = leafPath, color = leafColor)
    // Leaf vein
    drawLine(
        color = leafHighlight,
        start = Offset(leafCenter.x, leafCenter.y - 10.dp.toPx()),
        end = Offset(leafCenter.x, leafCenter.y + 10.dp.toPx()),
        strokeWidth = 1.2.dp.toPx()
    )

    // 2. Hanging cluster of mini grapes
    val grapeClusterBase = Offset(cx + (dir * 38.dp.toPx()), cy + 8.dp.toPx())
    val grapeOffsets = listOf(
        Offset(0f, 0f),
        Offset(-4f, 7f),
        Offset(4f, 7f),
        Offset(0f, 14f)
    )
    for (go in grapeOffsets) {
        val gp = Offset(grapeClusterBase.x + go.x * 2.5f, grapeClusterBase.y + go.y * 2.5f)
        drawCircle(color = grapeColor, radius = 4.dp.toPx(), center = gp)
        drawCircle(color = grapeHighlight, radius = 1.5.dp.toPx(), center = gp - Offset(1f, 1f))
    }

    // 3. Curly vine tendril spiral
    val tendrilStart = Offset(cx + (dir * 14.dp.toPx()), cy - 18.dp.toPx())
    val tendrilPath = Path().apply {
        moveTo(tendrilStart.x, tendrilStart.y)
        cubicTo(
            tendrilStart.x + (dir * 12.dp.toPx()), tendrilStart.y - 8.dp.toPx(),
            tendrilStart.x + (dir * 20.dp.toPx()), tendrilStart.y - 2.dp.toPx(),
            tendrilStart.x + (dir * 16.dp.toPx()), tendrilStart.y + 6.dp.toPx()
        )
    }
    drawPath(
        path = tendrilPath,
        color = Color(0xFF52B788),
        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    )
}

/**
 * Information plaque next to each grape node showing level name, target preview, and biome.
 */
@Composable
private fun LevelInfoPlaque(
    levelConfig: LevelConfig,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) ForestSurface.copy(alpha = 0.88f) else Color(0xFF141F18).copy(alpha = 0.7f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) ForestCardBorder else Color.DarkGray.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isUnlocked) levelConfig.biome.primaryColor else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = levelConfig.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isUnlocked) StarWhite else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Objective preview (Target score & Coins/Moss requirement)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🎯 ${levelConfig.targetScore} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) ForestEmeraldLight else Color.Gray,
                    fontSize = 10.sp
                )
                if (levelConfig.targetCoins > 0) {
                    Text(
                        text = "• ${levelConfig.targetCoins} 🪙",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUnlocked) ForestGold else Color.Gray,
                        fontSize = 10.sp
                    )
                }
                if (levelConfig.targetMoss > 0) {
                    Text(
                        text = "• ${levelConfig.targetMoss} 🌿",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUnlocked) LeafMoss else Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * Top header indicating the grapevine climbing infinitely into the starlight canopy.
 */
@Composable
private fun CelestialCanopyHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ForestSurfaceVariant.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmethystGem)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🍇 CELESTIAL CANOPY 🍇",
                    style = MaterialTheme.typography.titleSmall,
                    color = ForestGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The grapevine continues growing endlessly into the starlight! Defeat levels to climb higher.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
