package com.example.engine

import com.example.model.*
import kotlin.random.Random

data class BoardMatchResult(
    val matchedPositions: Set<Position>,
    val createdSpecials: Map<Position, SpecialType>,
    val pointsScored: Int,
    val coinsCollected: Int,
    val mossCleared: Int
)

object Match3Engine {
    const val ROWS = 8
    const val COLS = 8

    private var nextTileId = 1L

    fun getNextId(): Long = nextTileId++

    /**
     * Creates a new random board without any initial 3-in-a-row matches.
     */
    fun createBoard(mossPositions: List<Position> = emptyList()): Array<Array<BoardTile>> {
        val board = Array(ROWS) { r ->
            Array(COLS) { c ->
                BoardTile(
                    id = getNextId(),
                    type = ItemType.EMERALD,
                    row = r,
                    col = c,
                    hasMoss = mossPositions.contains(Position(r, c))
                )
            }
        }

        do {
            for (r in 0 until ROWS) {
                for (c in 0 until COLS) {
                    val forbidden = mutableSetOf<ItemType>()
                    if (r >= 2 && board[r - 1][c].type == board[r - 2][c].type) {
                        forbidden.add(board[r - 1][c].type)
                    }
                    if (c >= 2 && board[r][c - 1].type == board[r][c - 2].type) {
                        forbidden.add(board[r][c - 1].type)
                    }

                    val candidates = ItemType.allTypes.filter { it !in forbidden }
                    val chosen = if (candidates.isNotEmpty()) {
                        // 15% chance for a golden coin if not forbidden, else standard gems
                        if (ItemType.GOLD_COIN in candidates && Random.nextFloat() < 0.15f) {
                            ItemType.GOLD_COIN
                        } else {
                            val gemCandidates = candidates.filter { !it.isCoin }
                            if (gemCandidates.isNotEmpty()) gemCandidates.random() else candidates.random()
                        }
                    } else {
                        ItemType.EMERALD
                    }

                    board[r][c] = board[r][c].copy(
                        id = getNextId(),
                        type = chosen,
                        special = SpecialType.NONE
                    )
                }
            }
        } while (!hasPossibleMoves(board))

        return board
    }

    /**
     * Checks if two positions are orthogonal neighbors.
     */
    fun isAdjacent(p1: Position, p2: Position): Boolean {
        val dr = kotlin.math.abs(p1.row - p2.row)
        val dc = kotlin.math.abs(p1.col - p2.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    /**
     * Checks whether swapping p1 and p2 produces a match or a special combination.
     */
    fun canSwap(board: Array<Array<BoardTile>>, p1: Position, p2: Position): Boolean {
        if (!isAdjacent(p1, p2)) return false

        val t1 = board[p1.row][p1.col]
        val t2 = board[p2.row][p2.col]

        // Forest Star can be swapped with anything!
        if (t1.special == SpecialType.FOREST_STAR || t2.special == SpecialType.FOREST_STAR) {
            return true
        }

        // Two specials swapped together always trigger combo
        if (t1.special != SpecialType.NONE && t2.special != SpecialType.NONE) {
            return true
        }

        // Test swap
        val copy = copyBoard(board)
        copy[p1.row][p1.col] = t2.copy(row = p1.row, col = p1.col)
        copy[p2.row][p2.col] = t1.copy(row = p2.row, col = p2.col)

        return findMatches(copy).matchedPositions.isNotEmpty()
    }

    /**
     * Finds all matches on the current board and determines new specials.
     */
    fun findMatches(board: Array<Array<BoardTile>>, swapFocus: Position? = null): BoardMatchResult {
        val horizontalMatches = mutableListOf<List<Position>>()
        val verticalMatches = mutableListOf<List<Position>>()

        // Horizontal matches
        for (r in 0 until ROWS) {
            var matchStart = 0
            while (matchStart < COLS) {
                var matchEnd = matchStart + 1
                while (matchEnd < COLS && board[r][matchEnd].type == board[r][matchStart].type) {
                    matchEnd++
                }
                if (matchEnd - matchStart >= 3) {
                    val line = (matchStart until matchEnd).map { Position(r, it) }
                    horizontalMatches.add(line)
                }
                matchStart = matchEnd
            }
        }

        // Vertical matches
        for (c in 0 until COLS) {
            var matchStart = 0
            while (matchStart < ROWS) {
                var matchEnd = matchStart + 1
                while (matchEnd < ROWS && board[matchEnd][c].type == board[matchStart][c].type) {
                    matchEnd++
                }
                if (matchEnd - matchStart >= 3) {
                    val line = (matchStart until matchEnd).map { Position(it, c) }
                    verticalMatches.add(line)
                }
                matchStart = matchEnd
            }
        }

        val allMatched = mutableSetOf<Position>()
        val specialsToCreate = mutableMapOf<Position, SpecialType>()

        // Check for T or L intersections (Spore Bomb: 5 gems total matching horizontally and vertically)
        val combinedMatches = mutableListOf<Set<Position>>()
        for (h in horizontalMatches) {
            val hSet = h.toSet()
            val intersecting = verticalMatches.filter { v -> v.any { it in hSet } }
            if (intersecting.isNotEmpty()) {
                val combined = hSet.toMutableSet()
                intersecting.forEach { combined.addAll(it) }
                combinedMatches.add(combined)
                allMatched.addAll(combined)

                // The intersection point becomes a SPORE_BOMB
                val intersection = intersecting.first().first { it in hSet }
                val targetPos = if (swapFocus != null && swapFocus in combined) swapFocus else intersection
                specialsToCreate[targetPos] = SpecialType.SPORE_BOMB
            }
        }

        // Check horizontal lines
        for (h in horizontalMatches) {
            allMatched.addAll(h)
            if (!combinedMatches.any { cm -> h.all { it in cm } }) {
                if (h.size >= 5) {
                    val targetPos = if (swapFocus != null && swapFocus in h) swapFocus else h[h.size / 2]
                    specialsToCreate[targetPos] = SpecialType.FOREST_STAR
                } else if (h.size == 4) {
                    val targetPos = if (swapFocus != null && swapFocus in h) swapFocus else h[1]
                    specialsToCreate[targetPos] = SpecialType.ROW_BLAST
                }
            }
        }

        // Check vertical lines
        for (v in verticalMatches) {
            allMatched.addAll(v)
            if (!combinedMatches.any { cm -> v.all { it in cm } }) {
                if (v.size >= 5) {
                    val targetPos = if (swapFocus != null && swapFocus in v) swapFocus else v[v.size / 2]
                    specialsToCreate[targetPos] = SpecialType.FOREST_STAR
                } else if (v.size == 4) {
                    val targetPos = if (swapFocus != null && swapFocus in v) swapFocus else v[1]
                    specialsToCreate[targetPos] = SpecialType.COL_BLAST
                }
            }
        }

        // Expand matched positions to include special detonations (explosions, blasts)
        val fullyExpandedMatches = expandSpecialDetonations(board, allMatched)

        // Calculate points, coins, and moss cleared
        var points = 0
        var coins = 0
        var moss = 0

        for (pos in fullyExpandedMatches) {
            val tile = board[pos.row][pos.col]
            points += 50
            if (tile.type == ItemType.GOLD_COIN) {
                points += 150
                coins += 1
            } else if (tile.type == ItemType.MOON_COIN) {
                points += 200
                coins += 2
            }
            if (tile.hasMoss) {
                moss += 1
                points += 100
            }
        }

        return BoardMatchResult(
            matchedPositions = fullyExpandedMatches,
            createdSpecials = specialsToCreate,
            pointsScored = points,
            coinsCollected = coins,
            mossCleared = moss
        )
    }

    /**
     * Recursively expands detonations if any matched tile is a special power-up.
     */
    fun expandSpecialDetonations(
        board: Array<Array<BoardTile>>,
        initialMatches: Set<Position>
    ): Set<Position> {
        val visited = mutableSetOf<Position>()
        val queue = ArrayDeque<Position>()
        initialMatches.forEach {
            queue.add(it)
            visited.add(it)
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val tile = board[current.row][current.col]

            when (tile.special) {
                SpecialType.ROW_BLAST -> {
                    for (c in 0 until COLS) {
                        val p = Position(current.row, c)
                        if (visited.add(p)) queue.add(p)
                    }
                }
                SpecialType.COL_BLAST -> {
                    for (r in 0 until ROWS) {
                        val p = Position(r, current.col)
                        if (visited.add(p)) queue.add(p)
                    }
                }
                SpecialType.SPORE_BOMB -> {
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            val nr = current.row + dr
                            val nc = current.col + dc
                            if (nr in 0 until ROWS && nc in 0 until COLS) {
                                val p = Position(nr, nc)
                                if (visited.add(p)) queue.add(p)
                            }
                        }
                    }
                }
                SpecialType.FOREST_STAR -> {
                    // Explodes random color or clears all gems of most common type
                    val standardType = ItemType.standardGems.random()
                    for (r in 0 until ROWS) {
                        for (c in 0 until COLS) {
                            if (board[r][c].type == standardType) {
                                val p = Position(r, c)
                                if (visited.add(p)) queue.add(p)
                            }
                        }
                    }
                }
                SpecialType.NONE -> { /* no-op */ }
            }
        }

        return visited
    }

    /**
     * Handles special combo swaps (e.g. Forest Star + Gem, Bomb + Blaster).
     */
    fun executeSpecialCombo(
        board: Array<Array<BoardTile>>,
        p1: Position,
        p2: Position
    ): BoardMatchResult? {
        val t1 = board[p1.row][p1.col]
        val t2 = board[p2.row][p2.col]

        // Forest Star + Forest Star -> Wipe entire board
        if (t1.special == SpecialType.FOREST_STAR && t2.special == SpecialType.FOREST_STAR) {
            val allPos = (0 until ROWS).flatMap { r -> (0 until COLS).map { c -> Position(r, c) } }.toSet()
            return BoardMatchResult(
                matchedPositions = allPos,
                createdSpecials = emptyMap(),
                pointsScored = 5000,
                coinsCollected = allPos.count { board[it.row][it.col].type.isCoin },
                mossCleared = allPos.count { board[it.row][it.col].hasMoss }
            )
        }

        // Forest Star + Any Gem/Coin -> Clear all of that item type
        if (t1.special == SpecialType.FOREST_STAR || t2.special == SpecialType.FOREST_STAR) {
            val targetType = if (t1.special == SpecialType.FOREST_STAR) t2.type else t1.type
            val targetSpecial = if (t1.special == SpecialType.FOREST_STAR) t2.special else t1.special

            val affected = mutableSetOf(p1, p2)
            for (r in 0 until ROWS) {
                for (c in 0 until COLS) {
                    if (board[r][c].type == targetType) {
                        affected.add(Position(r, c))
                    }
                }
            }

            // If target was a special, turn all affected into that special before detonation
            val expanded = expandSpecialDetonations(board, affected)
            return BoardMatchResult(
                matchedPositions = expanded,
                createdSpecials = emptyMap(),
                pointsScored = expanded.size * 100 + 1000,
                coinsCollected = expanded.count { board[it.row][it.col].type.isCoin },
                mossCleared = expanded.count { board[it.row][it.col].hasMoss }
            )
        }

        // Bomb + Blaster -> Giant 3-row & 3-column cross clear
        if ((t1.special == SpecialType.SPORE_BOMB && (t2.special == SpecialType.ROW_BLAST || t2.special == SpecialType.COL_BLAST)) ||
            (t2.special == SpecialType.SPORE_BOMB && (t1.special == SpecialType.ROW_BLAST || t1.special == SpecialType.COL_BLAST))
        ) {
            val centerRow = p2.row
            val centerCol = p2.col
            val affected = mutableSetOf<Position>()
            for (r in (centerRow - 1)..(centerRow + 1)) {
                if (r in 0 until ROWS) {
                    for (c in 0 until COLS) affected.add(Position(r, c))
                }
            }
            for (c in (centerCol - 1)..(centerCol + 1)) {
                if (c in 0 until COLS) {
                    for (r in 0 until ROWS) affected.add(Position(r, c))
                }
            }
            val expanded = expandSpecialDetonations(board, affected)
            return BoardMatchResult(
                matchedPositions = expanded,
                createdSpecials = emptyMap(),
                pointsScored = expanded.size * 90 + 600,
                coinsCollected = expanded.count { board[it.row][it.col].type.isCoin },
                mossCleared = expanded.count { board[it.row][it.col].hasMoss }
            )
        }

        // Blaster + Blaster -> Cross clear row AND column
        if ((t1.special == SpecialType.ROW_BLAST || t1.special == SpecialType.COL_BLAST) &&
            (t2.special == SpecialType.ROW_BLAST || t2.special == SpecialType.COL_BLAST)
        ) {
            val affected = mutableSetOf<Position>()
            for (c in 0 until COLS) affected.add(Position(p2.row, c))
            for (r in 0 until ROWS) affected.add(Position(r, p2.col))
            val expanded = expandSpecialDetonations(board, affected)
            return BoardMatchResult(
                matchedPositions = expanded,
                createdSpecials = emptyMap(),
                pointsScored = expanded.size * 80 + 400,
                coinsCollected = expanded.count { board[it.row][it.col].type.isCoin },
                mossCleared = expanded.count { board[it.row][it.col].hasMoss }
            )
        }

        // Bomb + Bomb -> Massive 5x5 explosion
        if (t1.special == SpecialType.SPORE_BOMB && t2.special == SpecialType.SPORE_BOMB) {
            val affected = mutableSetOf<Position>()
            for (dr in -2..2) {
                for (dc in -2..2) {
                    val nr = p2.row + dr
                    val nc = p2.col + dc
                    if (nr in 0 until ROWS && nc in 0 until COLS) {
                        affected.add(Position(nr, nc))
                    }
                }
            }
            val expanded = expandSpecialDetonations(board, affected)
            return BoardMatchResult(
                matchedPositions = expanded,
                createdSpecials = emptyMap(),
                pointsScored = expanded.size * 80 + 500,
                coinsCollected = expanded.count { board[it.row][it.col].type.isCoin },
                mossCleared = expanded.count { board[it.row][it.col].hasMoss }
            )
        }

        return null
    }

    /**
     * Applies gravity: shifts surviving tiles down and spawns new tiles at the top.
     */
    fun applyGravityAndRefill(
        board: Array<Array<BoardTile>>,
        clearedPositions: Set<Position>,
        newSpecials: Map<Position, SpecialType>
    ): Array<Array<BoardTile>> {
        val newBoard = copyBoard(board)

        // Clear matched positions and remove moss underneath
        for (pos in clearedPositions) {
            val existing = newBoard[pos.row][pos.col]
            newBoard[pos.row][pos.col] = existing.copy(
                hasMoss = false,
                isClearing = true
            )
        }

        // Place new specials at their creation positions (if any)
        for ((pos, special) in newSpecials) {
            val current = newBoard[pos.row][pos.col]
            newBoard[pos.row][pos.col] = current.copy(
                id = getNextId(),
                special = special,
                isClearing = false,
                hasMoss = false
            )
        }

        // Shift each column down
        for (c in 0 until COLS) {
            var writeRow = ROWS - 1
            for (r in (ROWS - 1) downTo 0) {
                val current = newBoard[r][c]
                val wasCleared = clearedPositions.contains(Position(r, c)) && !newSpecials.containsKey(Position(r, c))

                if (!wasCleared) {
                    if (writeRow != r) {
                        newBoard[writeRow][c] = current.copy(row = writeRow, isClearing = false)
                    }
                    writeRow--
                }
            }

            // Fill empty slots at top with new gems/coins
            while (writeRow >= 0) {
                val hasMoss = newBoard[writeRow][c].hasMoss
                val coinRoll = Random.nextFloat()
                val randomType = when {
                    coinRoll < 0.15f -> ItemType.GOLD_COIN
                    coinRoll < 0.20f -> ItemType.MOON_COIN
                    else -> ItemType.standardGems.random()
                }

                newBoard[writeRow][c] = BoardTile(
                    id = getNextId(),
                    type = randomType,
                    special = SpecialType.NONE,
                    row = writeRow,
                    col = c,
                    hasMoss = hasMoss,
                    isClearing = false
                )
                writeRow--
            }
        }

        return newBoard
    }

    /**
     * Checks if at least one legal move exists on the board.
     */
    fun hasPossibleMoves(board: Array<Array<BoardTile>>): Boolean {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                // If any Forest Star is present, a move is always possible!
                if (board[r][c].special == SpecialType.FOREST_STAR) return true

                // Check right swap
                if (c + 1 < COLS) {
                    if (canSwap(board, Position(r, c), Position(r, c + 1))) return true
                }
                // Check down swap
                if (r + 1 < ROWS) {
                    if (canSwap(board, Position(r, c), Position(r + 1, c))) return true
                }
            }
        }
        return false
    }

    /**
     * Reshuffles the board when no moves are left, preserving moss and special tiles.
     */
    fun shuffleBoard(board: Array<Array<BoardTile>>): Array<Array<BoardTile>> {
        val flatTiles = mutableListOf<BoardTile>()
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                flatTiles.add(board[r][c])
            }
        }

        var shuffledBoard: Array<Array<BoardTile>>
        var attempts = 0
        do {
            flatTiles.shuffle()
            var index = 0
            shuffledBoard = Array(ROWS) { r ->
                Array(COLS) { c ->
                    val t = flatTiles[index++]
                    t.copy(
                        row = r,
                        col = c,
                        hasMoss = board[r][c].hasMoss,
                        isClearing = false,
                        isHighlighted = false
                    )
                }
            }
            attempts++
        } while ((findMatches(shuffledBoard).matchedPositions.isNotEmpty() || !hasPossibleMoves(shuffledBoard)) && attempts < 50)

        return shuffledBoard
    }

    private fun copyBoard(board: Array<Array<BoardTile>>): Array<Array<BoardTile>> {
        return Array(ROWS) { r ->
            Array(COLS) { c ->
                board[r][c].copy()
            }
        }
    }
}
