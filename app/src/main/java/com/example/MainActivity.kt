package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.ForestDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showHowToPlay by remember { mutableStateOf(false) }

                // System Back Button Handling
                when (uiState.currentScreen) {
                    GameScreen.MAIN_MENU -> {
                        // Let system handle exit
                    }
                    GameScreen.LEVEL_MAP -> {
                        BackHandler {
                            viewModel.navigateTo(GameScreen.MAIN_MENU)
                        }
                    }
                    GameScreen.GAMEPLAY -> {
                        BackHandler {
                            if (uiState.isZenMode) {
                                viewModel.navigateTo(GameScreen.MAIN_MENU)
                            } else {
                                viewModel.navigateTo(GameScreen.LEVEL_MAP)
                            }
                        }
                    }
                    GameScreen.SHOP -> {
                        BackHandler {
                            viewModel.navigateTo(GameScreen.MAIN_MENU)
                        }
                    }
                    GameScreen.HOW_TO_PLAY -> {
                        BackHandler {
                            viewModel.navigateTo(GameScreen.MAIN_MENU)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ForestDarkBackground
                ) {
                    when (uiState.currentScreen) {
                        GameScreen.MAIN_MENU -> {
                            MainMenuScreen(
                                playerCoins = uiState.playerCoins,
                                soundEnabled = uiState.soundEnabled,
                                hapticsEnabled = uiState.hapticsEnabled,
                                onPlayCampaign = { viewModel.navigateTo(GameScreen.LEVEL_MAP) },
                                onPlayZen = { viewModel.startZenMode() },
                                onOpenShop = { viewModel.navigateTo(GameScreen.SHOP) },
                                onOpenHowToPlay = { showHowToPlay = true },
                                onToggleSound = { viewModel.toggleSound() },
                                onToggleHaptics = { viewModel.toggleHaptics() }
                            )
                        }

                        GameScreen.LEVEL_MAP -> {
                            LevelMapScreen(
                                levelRecords = uiState.levelRecords,
                                playerCoins = uiState.playerCoins,
                                onLevelSelected = { levelNum -> viewModel.startLevel(levelNum) },
                                onBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                            )
                        }

                        GameScreen.GAMEPLAY -> {
                            GamePlayScreen(
                                viewModel = viewModel,
                                state = uiState
                            )
                        }

                        GameScreen.SHOP -> {
                            ShopScreen(
                                playerCoins = uiState.playerCoins,
                                powerUpCounts = uiState.powerUpCounts,
                                onBuyPowerUp = { powerUp -> viewModel.buyPowerUp(powerUp) },
                                onBack = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                            )
                        }

                        GameScreen.HOW_TO_PLAY -> {
                            // Can also be shown as a dialog or screen
                            HowToPlayModal(
                                onDismiss = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
                            )
                        }
                    }

                    // How to Play Dialog Overlay from anywhere
                    if (showHowToPlay) {
                        HowToPlayModal(
                            onDismiss = { showHowToPlay = false }
                        )
                    }
                }
            }
        }
    }
}
