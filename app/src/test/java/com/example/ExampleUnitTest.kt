package com.example

import com.example.engine.Match3Engine
import com.example.model.BoardTile
import com.example.model.ItemType
import com.example.model.LevelRepository
import com.example.model.Position
import com.example.model.SpecialType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun boardCreation_hasValidDimensionsAndMoves() {
        val board = Match3Engine.createBoard()
        assertEquals(8, board.size)
        assertEquals(8, board[0].size)

        // Board must have at least one valid move
        assertTrue(Match3Engine.hasPossibleMoves(board))

        // Initial board must have no pre-existing 3-in-a-row matches
        val initialMatches = Match3Engine.findMatches(board)
        assertTrue(initialMatches.matchedPositions.isEmpty())
    }

    @Test
    fun adjacency_check() {
        assertTrue(Match3Engine.isAdjacent(Position(2, 3), Position(2, 4)))
        assertTrue(Match3Engine.isAdjacent(Position(2, 3), Position(3, 3)))
        assertFalse(Match3Engine.isAdjacent(Position(2, 3), Position(3, 4))) // Diagonal is not adjacent
        assertFalse(Match3Engine.isAdjacent(Position(2, 3), Position(2, 3))) // Self is not adjacent
    }

    @Test
    fun match3_horizontalDetection() {
        val board = Array(8) { r ->
            Array(8) { c ->
                BoardTile(
                    id = (r * 8 + c).toLong(),
                    type = ItemType.EMERALD,
                    row = r,
                    col = c
                )
            }
        }
        // Fill row 0 with distinct types except cols 1,2,3 which match RUBY
        board[0][0] = board[0][0].copy(type = ItemType.TOPAZ)
        board[0][1] = board[0][1].copy(type = ItemType.RUBY)
        board[0][2] = board[0][2].copy(type = ItemType.RUBY)
        board[0][3] = board[0][3].copy(type = ItemType.RUBY)
        board[0][4] = board[0][4].copy(type = ItemType.SAPPHIRE)
        board[0][5] = board[0][5].copy(type = ItemType.AMETHYST)
        board[0][6] = board[0][6].copy(type = ItemType.GOLD_COIN)
        board[0][7] = board[0][7].copy(type = ItemType.TOPAZ)

        for (r in 1 until 8) {
            for (c in 0 until 8) {
                board[r][c] = board[r][c].copy(type = if ((r + c) % 2 == 0) ItemType.AMETHYST else ItemType.SAPPHIRE)
            }
        }

        val result = Match3Engine.findMatches(board)
        assertTrue(result.matchedPositions.contains(Position(0, 1)))
        assertTrue(result.matchedPositions.contains(Position(0, 2)))
        assertTrue(result.matchedPositions.contains(Position(0, 3)))
    }

    @Test
    fun levelRepository_containsAllBiomes() {
        assertEquals(12, LevelRepository.levels.size)
        val level1 = LevelRepository.getLevel(1)
        assertEquals(1, level1.levelNumber)
        assertTrue(level1.maxMoves > 0)
        assertTrue(level1.targetScore > 0)
    }

    @Test
    fun endlessGrapevineLevels_scaleDifficultyHigher() {
        val level12 = LevelRepository.getLevel(12)
        val level25 = LevelRepository.getLevel(25)
        val level50 = LevelRepository.getLevel(50)

        assertEquals(25, level25.levelNumber)
        assertEquals(50, level50.levelNumber)

        // Target score must get progressively higher
        assertTrue(level25.targetScore > level12.targetScore)
        assertTrue(level50.targetScore > level25.targetScore)

        // Coin requirement must scale up
        assertTrue(level25.targetCoins >= level12.targetCoins)
        assertTrue(level50.targetCoins >= level25.targetCoins)

        // Moves must remain strategic and positive
        assertTrue(level25.maxMoves in 16..26)
        assertTrue(level50.maxMoves in 16..26)
    }
}
