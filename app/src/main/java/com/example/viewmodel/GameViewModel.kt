package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.Match3Engine
import com.example.model.*
import com.example.util.GamePreferences
import com.example.util.SoundAndHaptics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameScreen {
    MAIN_MENU,
    LEVEL_MAP,
    GAMEPLAY,
    SHOP,
    HOW_TO_PLAY
}

data class GameUiState(
    val currentScreen: GameScreen = GameScreen.MAIN_MENU,
    val isZenMode: Boolean = false,
    val currentLevel: LevelConfig = LevelRepository.levels.first(),
    val board: Array<Array<BoardTile>> = emptyArray(),
    val score: Int = 0,
    val movesRemaining: Int = 20,
    val coinsCollectedInGame: Int = 0,
    val mossClearedInGame: Int = 0,
    val selectedTile: Position? = null,
    val activePowerUp: PowerUpType? = null,
    val powerUpSwapSource: Position? = null,
    val isAnimating: Boolean = false,
    val isPaused: Boolean = false,
    val isWon: Boolean = false,
    val isGameOver: Boolean = false,
    val isFrenzyActive: Boolean = false,
    val starsAwarded: Int = 0,
    val floatingPraise: String? = null,
    val playerCoins: Int = 150,
    val powerUpCounts: Map<PowerUpType, Int> = emptyMap(),
    val levelRecords: List<LevelRecord> = emptyList(),
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val activeEffects: List<BoardVisualEffect> = emptyList()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = GamePreferences(application)
    val feedback = SoundAndHaptics(application)

    private val _uiState = MutableStateFlow(
        GameUiState(
            playerCoins = prefs.coins,
            powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
            levelRecords = prefs.getAllLevelRecords(),
            soundEnabled = prefs.soundEnabled,
            hapticsEnabled = prefs.hapticsEnabled
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        feedback.isSoundEnabled = prefs.soundEnabled
        feedback.isHapticsEnabled = prefs.hapticsEnabled
    }

    fun navigateTo(screen: GameScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen, activePowerUp = null)
    }

    fun toggleSound() {
        val newVal = !prefs.soundEnabled
        prefs.soundEnabled = newVal
        feedback.isSoundEnabled = newVal
        _uiState.value = _uiState.value.copy(soundEnabled = newVal)
    }

    fun toggleHaptics() {
        val newVal = !prefs.hapticsEnabled
        prefs.hapticsEnabled = newVal
        feedback.isHapticsEnabled = newVal
        _uiState.value = _uiState.value.copy(hapticsEnabled = newVal)
    }

    fun startLevel(levelNumber: Int) {
        val config = LevelRepository.getLevel(levelNumber)
        val initialBoard = Match3Engine.createBoard(config.mossPattern)
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreen.GAMEPLAY,
            isZenMode = false,
            currentLevel = config,
            board = initialBoard,
            score = 0,
            movesRemaining = config.maxMoves,
            coinsCollectedInGame = 0,
            mossClearedInGame = 0,
            selectedTile = null,
            activePowerUp = null,
            powerUpSwapSource = null,
            isAnimating = false,
            isPaused = false,
            isWon = false,
            isGameOver = false,
            isFrenzyActive = false,
            starsAwarded = 0,
            floatingPraise = null,
            playerCoins = prefs.coins,
            powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
            levelRecords = prefs.getAllLevelRecords(),
            activeEffects = emptyList()
        )
    }

    fun startZenMode() {
        val initialBoard = Match3Engine.createBoard()
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreen.GAMEPLAY,
            isZenMode = true,
            board = initialBoard,
            score = 0,
            movesRemaining = 999,
            coinsCollectedInGame = 0,
            mossClearedInGame = 0,
            selectedTile = null,
            activePowerUp = null,
            powerUpSwapSource = null,
            isAnimating = false,
            isPaused = false,
            isWon = false,
            isGameOver = false,
            isFrenzyActive = false,
            floatingPraise = "Zen Harmony ✨",
            activeEffects = emptyList()
        )
    }

    fun pauseGame() {
        _uiState.value = _uiState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        _uiState.value = _uiState.value.copy(isPaused = false)
    }

    fun restartCurrentLevel() {
        if (_uiState.value.isZenMode) {
            startZenMode()
        } else {
            startLevel(_uiState.value.currentLevel.levelNumber)
        }
    }

    fun nextLevel() {
        val nextNum = _uiState.value.currentLevel.levelNumber + 1
        startLevel(nextNum)
    }

    fun selectPowerUp(powerUp: PowerUpType) {
        val current = _uiState.value.activePowerUp
        if (current == powerUp) {
            // Deselect
            _uiState.value = _uiState.value.copy(activePowerUp = null, powerUpSwapSource = null)
        } else {
            val count = prefs.getPowerUpCount(powerUp)
            if (count > 0) {
                if (powerUp == PowerUpType.FOREST_BREEZE) {
                    // Immediate trigger
                    useForestBreeze()
                } else {
                    _uiState.value = _uiState.value.copy(
                        activePowerUp = powerUp,
                        powerUpSwapSource = null,
                        selectedTile = null
                    )
                }
            } else {
                // If 0, navigate or show shop
                navigateTo(GameScreen.SHOP)
            }
        }
    }

    fun buyPowerUp(powerUp: PowerUpType) {
        val currentCoins = prefs.coins
        if (currentCoins >= powerUp.coinCost) {
            prefs.coins = currentCoins - powerUp.coinCost
            prefs.addPowerUp(powerUp, 1)
            feedback.playPowerUpUsed()
            _uiState.value = _uiState.value.copy(
                playerCoins = prefs.coins,
                powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) }
            )
        }
    }

    fun onTileClicked(pos: Position) {
        val state = _uiState.value
        if (state.isAnimating || state.isPaused || state.isWon || state.isGameOver) return

        // Check if an active power-up is being targeted
        if (state.activePowerUp != null) {
            handlePowerUpTarget(pos, state.activePowerUp)
            return
        }

        // Standard Match-3 Tile Click
        val selected = state.selectedTile
        if (selected == null) {
            // Select first tile
            _uiState.value = state.copy(selectedTile = pos)
            feedback.playTileSelect()
        } else if (selected == pos) {
            // If tapping the already-selected tile and it is a special, activate it!
            val tile = state.board[pos.row][pos.col]
            if (tile.special != SpecialType.NONE) {
                _uiState.value = state.copy(selectedTile = null)
                activateSpecialTile(pos)
            } else {
                // Deselect
                _uiState.value = state.copy(selectedTile = null)
            }
        } else if (Match3Engine.isAdjacent(selected, pos)) {
            // Attempt swap!
            attemptSwap(selected, pos)
        } else {
            // Change selection to new tile
            _uiState.value = state.copy(selectedTile = pos)
            feedback.playTileSelect()
        }
    }

    fun activateSpecialTile(pos: Position) {
        val state = _uiState.value
        if (state.isAnimating || state.isPaused || state.isWon || state.isGameOver) return
        val tile = state.board[pos.row][pos.col]
        if (tile.special == SpecialType.NONE) return

        val newMoves = if (state.isZenMode) 999 else (state.movesRemaining - 1).coerceAtLeast(0)
        _uiState.value = state.copy(selectedTile = null, movesRemaining = newMoves, isAnimating = true)

        detonatePositions(setOf(pos), emptyMap(), 250)
    }

    fun removeEffect(effectId: Long) {
        _uiState.value = _uiState.value.copy(
            activeEffects = _uiState.value.activeEffects.filter { it.id != effectId }
        )
    }


    fun onTileSwipe(from: Position, direction: SwipeDirection) {
        val state = _uiState.value
        if (state.isAnimating || state.isPaused || state.isWon || state.isGameOver) return

        val to = when (direction) {
            SwipeDirection.UP -> Position(from.row - 1, from.col)
            SwipeDirection.DOWN -> Position(from.row + 1, from.col)
            SwipeDirection.LEFT -> Position(from.row, from.col - 1)
            SwipeDirection.RIGHT -> Position(from.row, from.col + 1)
        }

        if (to.row in 0 until Match3Engine.ROWS && to.col in 0 until Match3Engine.COLS) {
            // Check if an active power-up is being targeted
            if (state.activePowerUp != null) {
                handlePowerUpTarget(from, state.activePowerUp)
                return
            }
            attemptSwap(from, to)
        }
    }

    private fun handlePowerUpTarget(pos: Position, powerUp: PowerUpType) {
        when (powerUp) {
            PowerUpType.PIXIE_WAND -> {
                if (prefs.usePowerUp(PowerUpType.PIXIE_WAND)) {
                    feedback.playExplosion()
                    _uiState.value = _uiState.value.copy(
                        activePowerUp = null,
                        powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
                        floatingPraise = "Pixie Smash! ✨"
                    )
                    detonatePositions(setOf(pos), emptyMap(), 250)
                }
            }
            PowerUpType.ROOT_SWAP -> {
                val source = _uiState.value.powerUpSwapSource
                if (source == null) {
                    _uiState.value = _uiState.value.copy(powerUpSwapSource = pos)
                    feedback.playTileSelect()
                } else if (source == pos) {
                    _uiState.value = _uiState.value.copy(powerUpSwapSource = null)
                } else if (Match3Engine.isAdjacent(source, pos)) {
                    if (prefs.usePowerUp(PowerUpType.ROOT_SWAP)) {
                        feedback.playPowerUpUsed()
                        _uiState.value = _uiState.value.copy(
                            activePowerUp = null,
                            powerUpSwapSource = null,
                            powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
                            floatingPraise = "Ent's Root Swap! 🌿"
                        )
                        // Execute forced swap without moves deduction
                        val b = _uiState.value.board
                        val t1 = b[source.row][source.col]
                        val t2 = b[pos.row][pos.col]
                        b[source.row][source.col] = t2.copy(row = source.row, col = source.col)
                        b[pos.row][pos.col] = t1.copy(row = pos.row, col = pos.col)
                        resolveBoardCascades(b, 1)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(powerUpSwapSource = pos)
                    feedback.playTileSelect()
                }
            }
            PowerUpType.SUNBEAM_STRIKE -> {
                if (prefs.usePowerUp(PowerUpType.SUNBEAM_STRIKE)) {
                    feedback.playExplosion()
                    _uiState.value = _uiState.value.copy(
                        activePowerUp = null,
                        powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
                        floatingPraise = "Solar Strike! ☀️"
                    )
                    val cross = mutableSetOf<Position>()
                    for (c in 0 until Match3Engine.COLS) cross.add(Position(pos.row, c))
                    for (r in 0 until Match3Engine.ROWS) cross.add(Position(r, pos.col))
                    detonatePositions(cross, emptyMap(), 800)
                }
            }
            PowerUpType.NECTAR_BLOOM -> {
                if (prefs.usePowerUp(PowerUpType.NECTAR_BLOOM)) {
                    feedback.playSpecialCreation()
                    val board = _uiState.value.board
                    board[pos.row][pos.col] = board[pos.row][pos.col].copy(
                        special = SpecialType.FOREST_STAR
                    )
                    _uiState.value = _uiState.value.copy(
                        activePowerUp = null,
                        powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
                        board = board,
                        floatingPraise = "Forest Star Bloomed! 🌸"
                    )
                }
            }
            PowerUpType.FOREST_BREEZE -> useForestBreeze()
        }
    }

    private fun useForestBreeze() {
        if (prefs.usePowerUp(PowerUpType.FOREST_BREEZE)) {
            feedback.playPowerUpUsed()
            val shuffled = Match3Engine.shuffleBoard(_uiState.value.board)
            _uiState.value = _uiState.value.copy(
                activePowerUp = null,
                board = shuffled,
                powerUpCounts = PowerUpType.entries.associateWith { prefs.getPowerUpCount(it) },
                floatingPraise = "Forest Breeze Whirlwind! 🍃"
            )
        }
    }

    private fun attemptSwap(p1: Position, p2: Position) {
        val board = _uiState.value.board
        val canSwap = Match3Engine.canSwap(board, p1, p2)

        _uiState.value = _uiState.value.copy(selectedTile = null)

        if (!canSwap) {
            feedback.playTileSelect()
            return
        }

        // Deduct move (if not in Zen mode)
        val newMoves = if (_uiState.value.isZenMode) 999 else (_uiState.value.movesRemaining - 1).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(movesRemaining = newMoves, isAnimating = true)

        // Check for special combo
        val specialComboResult = Match3Engine.executeSpecialCombo(board, p1, p2)
        if (specialComboResult != null) {
            val praiseText = if (specialComboResult.detonatedTreasureWipes.isNotEmpty()) {
                "TREASURY UNLEASHED! 👑"
            } else {
                "MYTHICAL COMBO! 🌟"
            }
            applyMatchResultAndCascade(specialComboResult, 1, praiseText)
            return
        }

        // Standard swap
        val t1 = board[p1.row][p1.col]
        val t2 = board[p2.row][p2.col]
        board[p1.row][p1.col] = t2.copy(row = p1.row, col = p1.col)
        board[p2.row][p2.col] = t1.copy(row = p2.row, col = p2.col)

        resolveBoardCascades(board, 1, swapFocus = p2)
    }

    private fun resolveBoardCascades(
        board: Array<Array<BoardTile>>,
        comboCount: Int,
        swapFocus: Position? = null
    ) {
        viewModelScope.launch {
            val matchResult = Match3Engine.findMatches(board, swapFocus)
            if (matchResult.matchedPositions.isNotEmpty()) {
                val praise = when (comboCount) {
                    1 -> if (matchResult.matchedPositions.size >= 5) "Awesome!" else null
                    2 -> "Sweet Magic! ✨"
                    3 -> "Super Combo! 🌟"
                    4 -> "Enchanted! 🔥"
                    else -> "UNSTOPPABLE NATURE! 🌿⚡"
                }

                applyMatchResultAndCascade(matchResult, comboCount, praise)
            } else {
                // No more matches: board has settled
                _uiState.value = _uiState.value.copy(isAnimating = false)
                checkGameStatusAfterTurn()
            }
        }
    }

    private fun applyMatchResultAndCascade(
        result: com.example.engine.BoardMatchResult,
        comboCount: Int,
        praise: String? = null
    ) {
        viewModelScope.launch {
            val newEffects = mutableListOf<BoardVisualEffect>()
            var effectIdGen = System.currentTimeMillis()

            var hasTreasureWipe = false
            if (result.detonatedTreasureWipes.isNotEmpty()) {
                hasTreasureWipe = true
                feedback.playTreasureOpening()
                result.detonatedTreasureWipes.forEach { pos ->
                    newEffects.add(BoardVisualEffect.FullCanvasTreasureWipe(effectIdGen++, pos.row, pos.col))
                }
            }

            if (result.detonatedRowFlashes.isNotEmpty() || result.detonatedColFlashes.isNotEmpty()) {
                feedback.playSwoosh()
                result.detonatedRowFlashes.forEach { row ->
                    newEffects.add(BoardVisualEffect.RowSliderFlash(effectIdGen++, row))
                }
                result.detonatedColFlashes.forEach { col ->
                    newEffects.add(BoardVisualEffect.ColSliderFlash(effectIdGen++, col))
                }
            }

            if (result.detonatedChainStackBursts.isNotEmpty()) {
                feedback.playCoinBurst()
                result.detonatedChainStackBursts.forEach { (pos, isCoin) ->
                    newEffects.add(BoardVisualEffect.ChainStackLightBurst(effectIdGen++, pos.row, pos.col, isCoin))
                }
            }

            // Standard match / creation chimes if no special audio event was played
            if (newEffects.isEmpty()) {
                if (result.createdSpecials.isNotEmpty()) {
                    feedback.playSpecialCreation()
                } else {
                    feedback.playMatch(comboCount)
                }
            }

            val currentScore = _uiState.value.score + result.pointsScored * comboCount
            val currentCoinsInGame = _uiState.value.coinsCollectedInGame + result.coinsCollected
            val currentMossCleared = _uiState.value.mossClearedInGame + result.mossCleared

            if (result.coinsCollected > 0) {
                prefs.coins = prefs.coins + result.coinsCollected
            }

            val effectivePraise = if (hasTreasureWipe) "TREASURY UNLEASHED! 👑" else praise

            _uiState.value = _uiState.value.copy(
                score = currentScore,
                coinsCollectedInGame = currentCoinsInGame,
                mossClearedInGame = currentMossCleared,
                playerCoins = prefs.coins,
                floatingPraise = effectivePraise,
                activeEffects = _uiState.value.activeEffects + newEffects
            )

            // Step 1: Let the effects and departure shine
            if (hasTreasureWipe) {
                delay(480) // Give player moment to see divine white-gold light wipe all canvas!
            } else {
                delay(200)
            }

            // Step 2: Apply gravity & refill (new gems & coins drop into place)
            val refilledBoard = Match3Engine.applyGravityAndRefill(
                _uiState.value.board,
                result.matchedPositions,
                result.createdSpecials
            )

            _uiState.value = _uiState.value.copy(board = refilledBoard)
            delay(240)

            // Cascade next wave
            resolveBoardCascades(refilledBoard, comboCount + 1)
        }
    }

    private fun detonatePositions(
        positions: Set<Position>,
        newSpecials: Map<Position, SpecialType>,
        bonusPoints: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnimating = true)
            val detInfo = Match3Engine.expandSpecialDetonations(_uiState.value.board, positions)
            val expanded = detInfo.affectedPositions

            var coins = 0
            var moss = 0
            for (p in expanded) {
                val t = _uiState.value.board[p.row][p.col]
                if (t.type.isCoin) coins++
                if (t.hasMoss) moss++
            }

            val result = com.example.engine.BoardMatchResult(
                matchedPositions = expanded,
                createdSpecials = newSpecials,
                pointsScored = bonusPoints + (expanded.size * 50),
                coinsCollected = coins,
                mossCleared = moss,
                detonatedRowFlashes = detInfo.rowFlashes,
                detonatedColFlashes = detInfo.colFlashes,
                detonatedChainStackBursts = detInfo.chainStackBursts,
                detonatedTreasureWipes = detInfo.treasureWipes
            )
            applyMatchResultAndCascade(result, 1)
        }
    }


    private fun checkGameStatusAfterTurn() {
        val state = _uiState.value
        if (state.isZenMode) {
            if (state.score > prefs.zenHighScore) {
                prefs.zenHighScore = state.score
            }
            // Auto shuffle if no moves exist
            if (!Match3Engine.hasPossibleMoves(state.board)) {
                val shuffled = Match3Engine.shuffleBoard(state.board)
                _uiState.value = state.copy(board = shuffled, floatingPraise = "Reshuffled! 🍃")
            }
            return
        }

        val config = state.currentLevel
        val scoreMet = state.score >= config.targetScore
        val coinsMet = state.coinsCollectedInGame >= config.targetCoins
        val mossMet = state.mossClearedInGame >= config.targetMoss

        if (scoreMet && coinsMet && mossMet) {
            // Level is won!
            if (state.movesRemaining > 0 && !state.isFrenzyActive) {
                // Trigger Forest Frenzy: remaining moves burst into bonus points & coins!
                triggerForestFrenzy()
            } else {
                finalizeLevelWon()
            }
        } else if (state.movesRemaining <= 0) {
            // Level lost
            feedback.playLevelLost()
            _uiState.value = state.copy(isGameOver = true)
        } else {
            // Check for stalemate
            if (!Match3Engine.hasPossibleMoves(state.board)) {
                val shuffled = Match3Engine.shuffleBoard(state.board)
                _uiState.value = state.copy(board = shuffled, floatingPraise = "No Moves - Forest Shuffle! 🍃")
            }
        }
    }

    private fun triggerForestFrenzy() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isFrenzyActive = true,
                isAnimating = true,
                floatingPraise = "FOREST FRENZY! 🎉"
            )
            delay(500)

            while (_uiState.value.movesRemaining > 0) {
                val b = _uiState.value.board
                val randomPos = Position(Random.nextInt(Match3Engine.ROWS), Random.nextInt(Match3Engine.COLS))
                // Convert to blast
                b[randomPos.row][randomPos.col] = b[randomPos.row][randomPos.col].copy(
                    special = if (Random.nextBoolean()) SpecialType.ROW_BLAST else SpecialType.COL_BLAST
                )

                _uiState.value = _uiState.value.copy(
                    movesRemaining = _uiState.value.movesRemaining - 1,
                    board = b
                )
                feedback.playSpecialCreation()
                delay(200)

                // Detonate
                detonatePositions(setOf(randomPos), emptyMap(), 300)
                delay(400)
            }

            finalizeLevelWon()
        }
    }

    private fun finalizeLevelWon() {
        val state = _uiState.value
        val config = state.currentLevel

        val stars = when {
            state.score >= config.threeStarScore -> 3
            state.score >= config.twoStarScore -> 2
            else -> 1
        }

        // Award bonus completion coins
        val bonusCoins = stars * 15
        prefs.coins = prefs.coins + bonusCoins
        prefs.saveLevelCompletion(config.levelNumber, stars, state.score)

        feedback.playLevelWon()
        _uiState.value = state.copy(
            isWon = true,
            isAnimating = false,
            isFrenzyActive = false,
            starsAwarded = stars,
            playerCoins = prefs.coins,
            levelRecords = prefs.getAllLevelRecords()
        )
    }
}
