package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.engine.Match3Engine
import com.example.model.Position
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Forest Backdrop
        Image(
            painter = painterResource(id = R.drawable.forest_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent dark wash for game board contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ForestDarkBackground.copy(alpha = 0.75f),
                            ForestDarkBackground.copy(alpha = 0.55f),
                            ForestDarkBackground.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Ambient Fireflies
        ForestFireflies()

        // Main Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            ForestHeaderBar(
                levelConfig = state.currentLevel,
                score = state.score,
                movesRemaining = state.movesRemaining,
                coinsCollected = state.coinsCollectedInGame,
                mossCleared = state.mossClearedInGame,
                onPauseClick = { viewModel.pauseGame() }
            )

            // Board Container (Responsive Aspect-ratio square)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Game Board Grid Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .testTag("game_board_grid"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ForestDarkBackground.copy(alpha = 0.90f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ForestCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (state.board.isNotEmpty()) {
                            for (r in 0 until Match3Engine.ROWS) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (c in 0 until Match3Engine.COLS) {
                                        val pos = Position(r, c)
                                        val tile = state.board[r][c]
                                        val isSelected = state.selectedTile == pos || state.powerUpSwapSource == pos
                                        val isTargetMode = state.activePowerUp != null

                                        GemTileView(
                                            tile = tile,
                                            isSelected = isSelected,
                                            isTargetMode = isTargetMode,
                                            onClick = { viewModel.onTileClicked(pos) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Praise Overlay (Combos, etc.)
                FloatingPraiseOverlay(praiseText = state.floatingPraise)
            }

            // Power-up Toolbelt at bottom
            PowerUpBar(
                powerUpCounts = state.powerUpCounts,
                activePowerUp = state.activePowerUp,
                onPowerUpSelected = { viewModel.selectPowerUp(it) }
            )
        }

        // --- Modals ---

        // Pause Modal
        if (state.isPaused) {
            PauseDialog(
                soundEnabled = state.soundEnabled,
                hapticsEnabled = state.hapticsEnabled,
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.restartCurrentLevel() },
                onQuit = { viewModel.navigateTo(com.example.viewmodel.GameScreen.LEVEL_MAP) },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptics = { viewModel.toggleHaptics() }
            )
        }

        // Victory Modal
        if (state.isWon) {
            VictoryDialog(
                stars = state.starsAwarded,
                score = state.score,
                coinsEarned = state.coinsCollectedInGame + (state.starsAwarded * 15),
                onNextLevel = { viewModel.nextLevel() },
                onReplay = { viewModel.restartCurrentLevel() },
                onMap = { viewModel.navigateTo(com.example.viewmodel.GameScreen.LEVEL_MAP) }
            )
        }

        // Game Over Modal
        if (state.isGameOver) {
            GameOverDialog(
                score = state.score,
                targetScore = state.currentLevel.targetScore,
                onTryAgain = { viewModel.restartCurrentLevel() },
                onMap = { viewModel.navigateTo(com.example.viewmodel.GameScreen.LEVEL_MAP) }
            )
        }
    }
}

@Composable
private fun PauseDialog(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) { onResume() },
            contentAlignment = Alignment.Center
        ) {
            // The Vine-Framed Wooden Board Container
            Box(
                modifier = Modifier
                    .widthIn(max = 390.dp)
                    .fillMaxWidth(0.92f)
                    .aspectRatio(1182f / 1330f)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { /* consume clicks inside board to avoid dismiss */ }
                    .testTag("pause_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Background image: Provided file_0000000074e881fa9baa79e8201bf7d1_1bc40558.png
                Image(
                    painter = painterResource(id = R.drawable.bg_pause_frame),
                    contentDescription = "Pause Board",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Foreground Content inside the carved wooden plaque
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 44.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Area: Just "PAUSED" with no subtitle
                    Text(
                        text = "PAUSED",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            shadow = Shadow(
                                color = Color(0xFF1E0E03),
                                offset = Offset(2f, 3f),
                                blurRadius = 6f
                            )
                        ),
                        color = ForestGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Button group with correct 3.33:1 aspect ratio so wood texture & rounded ends are not stretched
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WoodPlankButton(
                            text = "RESUME",
                            icon = Icons.Default.PlayArrow,
                            onClick = onResume,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f),
                            testTag = "resume_button"
                        )

                        WoodPlankButton(
                            text = "RESTART",
                            icon = Icons.Default.Refresh,
                            onClick = onRestart,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f),
                            testTag = "restart_button"
                        )

                        WoodPlankButton(
                            text = "RETURN TO MAP",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = onQuit,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f),
                            testTag = "quit_button"
                        )
                    }

                    // Sound & Haptics Ornate Wooden Buttons with just ON / OFF text
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WoodToggleOptionButton(
                            imageRes = R.drawable.btn_wood_sound,
                            enabled = soundEnabled,
                            onToggle = onToggleSound,
                            modifier = Modifier
                                .width(108.dp)
                                .aspectRatio(1072f / 700f),
                            testTag = "toggle_sound_button"
                        )

                        WoodToggleOptionButton(
                            imageRes = R.drawable.btn_wood_haptic,
                            enabled = hapticsEnabled,
                            onToggle = onToggleHaptics,
                            modifier = Modifier
                                .width(108.dp)
                                .aspectRatio(1035f / 666f),
                            testTag = "toggle_haptics_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VictoryDialog(
    stars: Int,
    score: Int,
    coinsEarned: Int,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onMap: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 390.dp)
                    .fillMaxWidth(0.92f)
                    .aspectRatio(1182f / 1330f)
                    .scale(scaleAnim.value)
                    .testTag("victory_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Background image: Provided vine-framed wooden board (bg_pause_frame)
                Image(
                    painter = painterResource(id = R.drawable.bg_pause_frame),
                    contentDescription = "Victory Board",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Foreground Content inside the carved wooden plaque
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 44.dp, end = 44.dp, top = 26.dp, bottom = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Stars Celebration Row with small "LEVEL COMPLETE" beneath
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..3) {
                                val active = i <= stars
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = if (active) "Star $i" else null,
                                    tint = if (active) ForestGold else Color(0xFF2C1E10).copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(if (i == 2) 46.dp else 36.dp)
                                        .offset(y = if (i == 2) (-4).dp else 0.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Small "LEVEL COMPLETE" text at the bottom of the stars
                        Text(
                            text = "LEVEL COMPLETE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                shadow = Shadow(
                                    color = Color(0xFF1E0E03),
                                    offset = Offset(1.5f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            color = ForestGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Score & Coins Won
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Score: $score",
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(
                                    color = Color(0xFF1E0F04),
                                    offset = Offset(1.5f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            color = StarWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF231508).copy(alpha = 0.88f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ForestGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_forest_gold_coin),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+$coinsEarned Coins Won!",
                                    color = ForestGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Buttons Column - elevated slightly so it won't touch the bottom vine border
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Next Level Button
                        WoodPlankButton(
                            text = "NEXT LEVEL",
                            onClick = onNextLevel,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f),
                            testTag = "next_level_button"
                        )

                        // Replay and Map Button Row with correct text arrangement
                        Row(
                            modifier = Modifier.width(204.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WoodPlankButton(
                                text = "REPLAY",
                                icon = Icons.Default.Refresh,
                                onClick = onReplay,
                                fontSize = 12.sp,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                testTag = "replay_button"
                            )

                            WoodPlankButton(
                                text = "MAP",
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                onClick = onMap,
                                fontSize = 12.sp,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                testTag = "map_button"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameOverDialog(
    score: Int,
    targetScore: Int,
    onTryAgain: () -> Unit,
    onMap: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 390.dp)
                    .fillMaxWidth(0.92f)
                    .aspectRatio(1182f / 1330f)
                    .testTag("game_over_dialog"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_pause_frame),
                    contentDescription = "Game Over Board",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 44.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "OUT OF MOVES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                shadow = Shadow(
                                    color = Color(0xFF1E0E03),
                                    offset = Offset(2f, 3f),
                                    blurRadius = 6f
                                )
                            ),
                            color = RubyGem,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "The forest magic faded this turn.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                shadow = Shadow(
                                    color = Color(0xFF1E0F04),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 3f
                                )
                            ),
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "Score: $score / $targetScore",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color(0xFF1E0F04),
                                offset = Offset(1.5f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WoodPlankButton(
                            text = "TRY AGAIN",
                            icon = Icons.Default.Refresh,
                            onClick = onTryAgain,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f),
                            testTag = "try_again_button"
                        )

                        WoodPlankButton(
                            text = "RETURN TO MAP",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = onMap,
                            modifier = Modifier
                                .width(204.dp)
                                .aspectRatio(2141f / 642f)
                        )
                    }
                }
            }
        }
    }
}
