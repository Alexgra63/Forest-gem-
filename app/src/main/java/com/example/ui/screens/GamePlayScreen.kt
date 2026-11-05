package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    Dialog(onDismissRequest = onResume) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ForestDarkBackground,
            border = androidx.compose.foundation.BorderStroke(2.dp, ForestCardBorder),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("pause_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PAUSED",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestEmeraldDark)
                ) {
                    Text("RESUME", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestEmerald)
                ) {
                    Text("RESTART LEVEL", color = StarWhite, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onQuit,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("RETURN TO MAP", color = TextMuted)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audio / Haptics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onToggleSound) {
                        Text(
                            text = if (soundEnabled) "🔊 Sound On" else "🔇 Sound Off",
                            color = if (soundEnabled) ForestGold else Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = onToggleHaptics) {
                        Text(
                            text = if (hapticsEnabled) "📳 Haptics On" else "📴 Haptics Off",
                            color = if (hapticsEnabled) ForestEmeraldLight else Color.Gray,
                            fontSize = 12.sp
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

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ForestDarkBackground,
            border = androidx.compose.foundation.BorderStroke(2.dp, ForestGold),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .scale(scaleAnim.value)
                .testTag("victory_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVEL COMPLETE!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ForestGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stars Celebration Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val active = i <= stars
                        Box(
                            modifier = Modifier
                                .size(if (i == 2) 58.dp else 48.dp)
                                .clip(CircleShape)
                                .background(if (active) ForestGoldDark.copy(alpha = 0.3f) else ForestSurfaceVariant)
                                .border(
                                    2.dp,
                                    if (active) ForestGold else Color.Gray.copy(alpha = 0.4f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (active) ForestGold else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier.size(if (i == 2) 36.dp else 28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleMedium,
                    color = StarWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ForestSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+$coinsEarned Coins Won!",
                            color = ForestGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("next_level_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestEmeraldDark),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestEmeraldLight)
                ) {
                    Text("NEXT LEVEL", fontWeight = FontWeight.Black, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ForestEmerald)
                    ) {
                        Text("Replay", color = StarWhite)
                    }

                    OutlinedButton(
                        onClick = onMap,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text("Map", color = TextMuted)
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
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ForestDarkBackground,
            border = androidx.compose.foundation.BorderStroke(2.dp, RubyGem),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OUT OF MOVES",
                    style = MaterialTheme.typography.headlineSmall,
                    color = RubyGem,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The forest magic faded this turn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Score: $score / $targetScore",
                    style = MaterialTheme.typography.titleMedium,
                    color = StarWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTryAgain,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("try_again_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RubyGem)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("TRY AGAIN", fontWeight = FontWeight.Black, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onMap,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("RETURN TO MAP", color = TextMuted)
                }
            }
        }
    }
}
