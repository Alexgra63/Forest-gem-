package com.example.model

object LevelRepository {
    private val handcraftedLevels: List<LevelConfig> = listOf(
        // Biome 1: Whispering Grove (Introduction to Gem & Coin matching)
        LevelConfig(
            levelNumber = 1,
            name = "Verdant Awakening",
            biome = Biome.WHISPERING_GROVE,
            maxMoves = 24,
            targetScore = 1500,
            targetCoins = 4,
            targetMoss = 0,
            oneStarScore = 1500,
            twoStarScore = 2500,
            threeStarScore = 4000
        ),
        LevelConfig(
            levelNumber = 2,
            name = "Fairy Spring",
            biome = Biome.WHISPERING_GROVE,
            maxMoves = 22,
            targetScore = 2800,
            targetCoins = 6,
            targetMoss = 0,
            oneStarScore = 2800,
            twoStarScore = 4200,
            threeStarScore = 6000
        ),
        LevelConfig(
            levelNumber = 3,
            name = "Ancient Glade",
            biome = Biome.WHISPERING_GROVE,
            maxMoves = 20,
            targetScore = 3500,
            targetCoins = 8,
            targetMoss = 4,
            oneStarScore = 3500,
            twoStarScore = 5000,
            threeStarScore = 7500,
            mossPattern = listOf(
                Position(3, 3), Position(3, 4), Position(4, 3), Position(4, 4)
            )
        ),

        // Biome 2: Luminescent Hollow (Moss clearing challenges)
        LevelConfig(
            levelNumber = 4,
            name = "Mossy Pathways",
            biome = Biome.LUMINESCENT_HOLLOW,
            maxMoves = 25,
            targetScore = 4000,
            targetCoins = 8,
            targetMoss = 8,
            oneStarScore = 4000,
            twoStarScore = 6500,
            threeStarScore = 9000,
            mossPattern = listOf(
                Position(2, 2), Position(2, 5), Position(5, 2), Position(5, 5),
                Position(3, 3), Position(3, 4), Position(4, 3), Position(4, 4)
            )
        ),
        LevelConfig(
            levelNumber = 5,
            name = "Glowshroom Cave",
            biome = Biome.LUMINESCENT_HOLLOW,
            maxMoves = 22,
            targetScore = 5000,
            targetCoins = 10,
            targetMoss = 12,
            oneStarScore = 5000,
            twoStarScore = 8000,
            threeStarScore = 12000,
            mossPattern = (2..5).flatMap { r -> (2..5).map { c -> Position(r, c) } }
        ),
        LevelConfig(
            levelNumber = 6,
            name = "Bioluminescent Lake",
            biome = Biome.LUMINESCENT_HOLLOW,
            maxMoves = 24,
            targetScore = 6500,
            targetCoins = 12,
            targetMoss = 14,
            oneStarScore = 6500,
            twoStarScore = 10000,
            threeStarScore = 14500,
            mossPattern = listOf(
                Position(1, 1), Position(1, 6), Position(6, 1), Position(6, 6),
                Position(2, 2), Position(2, 5), Position(5, 2), Position(5, 5),
                Position(3, 2), Position(3, 5), Position(4, 2), Position(4, 5),
                Position(3, 3), Position(4, 4)
            )
        ),

        // Biome 3: Moonlit Sanctuary (High coin demands and strategic blasters)
        LevelConfig(
            levelNumber = 7,
            name = "Silver Birch Way",
            biome = Biome.MOONLIT_SANCTUARY,
            maxMoves = 22,
            targetScore = 7500,
            targetCoins = 15,
            targetMoss = 10,
            oneStarScore = 7500,
            twoStarScore = 11500,
            threeStarScore = 16000,
            mossPattern = listOf(
                Position(0, 3), Position(0, 4), Position(7, 3), Position(7, 4),
                Position(3, 0), Position(4, 0), Position(3, 7), Position(4, 7),
                Position(3, 3), Position(4, 4)
            )
        ),
        LevelConfig(
            levelNumber = 8,
            name = "Nightbloom Shrine",
            biome = Biome.MOONLIT_SANCTUARY,
            maxMoves = 20,
            targetScore = 9000,
            targetCoins = 16,
            targetMoss = 16,
            oneStarScore = 9000,
            twoStarScore = 14000,
            threeStarScore = 19000,
            mossPattern = listOf(
                Position(1, 3), Position(1, 4), Position(6, 3), Position(6, 4),
                Position(3, 1), Position(4, 1), Position(3, 6), Position(4, 6),
                Position(2, 2), Position(2, 5), Position(5, 2), Position(5, 5),
                Position(3, 3), Position(3, 4), Position(4, 3), Position(4, 4)
            )
        ),
        LevelConfig(
            levelNumber = 9,
            name = "Starfall Altar",
            biome = Biome.MOONLIT_SANCTUARY,
            maxMoves = 25,
            targetScore = 11000,
            targetCoins = 20,
            targetMoss = 18,
            oneStarScore = 11000,
            twoStarScore = 17000,
            threeStarScore = 23000,
            mossPattern = (1..6).flatMap { r ->
                listOf(Position(r, 1), Position(r, 6), Position(1, r), Position(6, r))
            }.distinct().take(18)
        ),

        // Biome 4: Elder Tree Heart (Master challenges with dense moss & epic targets)
        LevelConfig(
            levelNumber = 10,
            name = "Root of Ancients",
            biome = Biome.ELDER_TREE_HEART,
            maxMoves = 26,
            targetScore = 13500,
            targetCoins = 22,
            targetMoss = 20,
            oneStarScore = 13500,
            twoStarScore = 20000,
            threeStarScore = 28000,
            mossPattern = (1..6).flatMap { r ->
                (1..6).filter { c -> (r + c) % 2 == 0 }.map { c -> Position(r, c) }
            }.take(20)
        ),
        LevelConfig(
            levelNumber = 11,
            name = "Primal Sap Spring",
            biome = Biome.ELDER_TREE_HEART,
            maxMoves = 25,
            targetScore = 16000,
            targetCoins = 25,
            targetMoss = 22,
            oneStarScore = 16000,
            twoStarScore = 24000,
            threeStarScore = 32000,
            mossPattern = (1..6).flatMap { r ->
                (1..6).filter { c -> r in 2..5 || c in 2..5 }.map { c -> Position(r, c) }
            }.take(22)
        ),
        LevelConfig(
            levelNumber = 12,
            name = "The Forest Heart",
            biome = Biome.ELDER_TREE_HEART,
            maxMoves = 28,
            targetScore = 20000,
            targetCoins = 30,
            targetMoss = 24,
            oneStarScore = 20000,
            twoStarScore = 30000,
            threeStarScore = 42000,
            mossPattern = (1..6).flatMap { r -> (1..6).map { c -> Position(r, c) } }.take(24)
        )
    )

    // For backwards compatibility
    val levels: List<LevelConfig> get() = handcraftedLevels

    /**
     * Procedural endless title generator based on level number and theme
     */
    private val endlessPrefixes = listOf(
        "Skyward", "Whispering", "Canopy", "Elder", "Mystic", "Sunburst",
        "Moonlit", "Verdant", "Thorned", "Crystal", "Twilight", "Celestial",
        "Ancient", "Golden", "Emerald", "Bramble", "Shadow", "Starlight"
    )
    private val endlessSuffixes = listOf(
        "Tendril", "Bower", "Arbor", "Vinecrest", "Grape Hollow", "Grove",
        "Trellis", "Vine Peak", "Canopy", "Sanctuary", "Orchard", "Spire",
        "Sap Spring", "Glade", "Thicket", "Overgrowth", "Altar", "Bough"
    )

    /**
     * Dynamically retrieves handcrafted or procedurally generated endless levels.
     * Each successive level increases in challenge: higher scores, tighter moves,
     * more required coin collection, and intricate moss patterns.
     */
    fun getLevel(number: Int): LevelConfig {
        if (number in 1..handcraftedLevels.size) {
            return handcraftedLevels[number - 1]
        }

        // Procedural generation for endless climbing levels (13, 14, 15, ... 1000+)
        val excess = number - 12
        val biomeIndex = ((number - 1) / 4) % Biome.entries.size
        val biome = Biome.entries[biomeIndex]

        val prefix = endlessPrefixes[(number * 7) % endlessPrefixes.size]
        val suffix = endlessSuffixes[(number * 13) % endlessSuffixes.size]
        val name = "$prefix $suffix"

        // Scaling Difficulty:
        // 1. Target Score increases progressively
        val baseScore = 20000 + (excess * 2400)
        val targetScore = baseScore + (number % 5) * 500

        // 2. Moves become tighter and require smarter special combinations (16 to 26 moves)
        val maxMoves = when (number % 5) {
            0 -> 18 // Boss challenge level
            1 -> 24 // Cascade breather
            2 -> 20 // Strategic puzzle
            3 -> 22 // Standard vine
            else -> 17 // Fast-paced speed challenge
        }

        // 3. Coins to collect scale up with excess levels
        val targetCoins = (26 + (excess * 1.5).toInt()).coerceAtMost(50)

        // 4. Moss tiles pattern gets denser
        val mossCount = (20 + (excess * 0.8).toInt()).coerceAtMost(36)
        val patternType = number % 4
        val mossPositions = when (patternType) {
            0 -> {
                // Checkerboard pattern
                (1..6).flatMap { r ->
                    (1..6).filter { c -> (r + c + number) % 2 == 0 }.map { c -> Position(r, c) }
                }.take(mossCount)
            }
            1 -> {
                // Outer perimeter borders
                (1..6).flatMap { r ->
                    listOf(Position(r, 1), Position(r, 6), Position(1, r), Position(6, r))
                }.distinct().take(mossCount)
            }
            2 -> {
                // Diamond cluster in center
                (1..6).flatMap { r ->
                    (1..6).filter { c -> kotlin.math.abs(r - 3.5f) + kotlin.math.abs(c - 3.5f) <= 3f }
                        .map { c -> Position(r, c) }
                }.take(mossCount)
            }
            else -> {
                // Cross / Vine X shape
                (0..7).flatMap { i ->
                    listOf(Position(i, i), Position(i, 7 - i))
                }.distinct().take(mossCount)
            }
        }

        val oneStar = targetScore
        val twoStar = (targetScore * 1.45f).toInt()
        val threeStar = (targetScore * 1.95f).toInt()

        return LevelConfig(
            levelNumber = number,
            name = name,
            biome = biome,
            maxMoves = maxMoves,
            targetScore = targetScore,
            targetCoins = targetCoins,
            targetMoss = mossPositions.size,
            oneStarScore = oneStar,
            twoStarScore = twoStar,
            threeStarScore = threeStar,
            mossPattern = mossPositions
        )
    }

    /**
     * Generates a contiguous list of levels from 1 up to [maxLevel].
     */
    fun getLevelsUpTo(maxLevel: Int): List<LevelConfig> {
        val total = maxLevel.coerceAtLeast(12)
        return (1..total).map { getLevel(it) }
    }
}
