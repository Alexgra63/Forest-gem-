package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

/**
 * The elemental items on the game board: enchanted gems and mystical coins.
 */
enum class ItemType(
    val displayName: String,
    val primaryColor: Color,
    val accentColor: Color,
    val isCoin: Boolean,
    val drawableRes: Int
) {
    EMERALD("Emerald Gem", EmeraldGem, ForestEmeraldLight, false, com.example.R.drawable.gem_emerald_item),
    RUBY("Ruby Sunstone", RubyGem, Color(0xFFFF85A1), false, com.example.R.drawable.gem_ruby_item),
    SAPPHIRE("Sapphire Tear", SapphireGem, Color(0xFF90E0EF), false, com.example.R.drawable.gem_sapphire_item),
    AMETHYST("Amethyst Shard", AmethystGem, Color(0xFFE0AAFF), false, com.example.R.drawable.gem_amethyst_item),
    TOPAZ("Topaz Amber", TopazGem, Color(0xFFFFD166), false, com.example.R.drawable.gem_topaz_item),
    GOLD_COIN("Sun Tree Coin", ForestGold, Color(0xFFFFF3B0), true, com.example.R.drawable.coin_gold_item),
    MOON_COIN("Moon Leaf Coin", ForestSilver, Color(0xFFFFFFFF), true, com.example.R.drawable.coin_moon_item);

    companion object {
        val standardGems = listOf(EMERALD, RUBY, SAPPHIRE, AMETHYST, TOPAZ)
        val allTypes = listOf(EMERALD, RUBY, SAPPHIRE, AMETHYST, TOPAZ, GOLD_COIN)
        val allWithMoon = entries.toList()
    }
}

/**
 * Special power-ups created through matches.
 */
enum class SpecialType(val label: String) {
    NONE("Normal"),
    ROW_BLAST("Horizontal Nature Ray"),
    COL_BLAST("Vertical Nature Ray"),
    SPORE_BOMB("Spore Burst (3x3)"),
    FOREST_STAR("Prismatic Tree Star")
}

/**
 * Interactive power-ups usable from the player's belt.
 */
enum class PowerUpType(
    val id: String,
    val title: String,
    val description: String,
    val coinCost: Int,
    val iconSymbol: String
) {
    PIXIE_WAND(
        id = "pixie_wand",
        title = "Pixie Wand",
        description = "Shatter any single gem or obstacle instantly without using a move.",
        coinCost = 60,
        iconSymbol = "✨"
    ),
    ROOT_SWAP(
        id = "root_swap",
        title = "Root Swap",
        description = "Force swap any two adjacent tiles even if they do not match.",
        coinCost = 90,
        iconSymbol = "🌿"
    ),
    FOREST_BREEZE(
        id = "forest_breeze",
        title = "Forest Breeze",
        description = "Summon a whirlwind of autumn leaves to reshuffle the entire board.",
        coinCost = 110,
        iconSymbol = "🍃"
    ),
    SUNBEAM_STRIKE(
        id = "sunbeam_strike",
        title = "Sunbeam Strike",
        description = "Blast an entire row and column in a brilliant flash of solar magic.",
        coinCost = 150,
        iconSymbol = "☀️"
    ),
    NECTAR_BLOOM(
        id = "nectar_bloom",
        title = "Nectar Bloom",
        description = "Infuse any tile into a Prismatic Tree Star to wipe all gems of one type.",
        coinCost = 200,
        iconSymbol = "🌸"
    )
}

/**
 * Board tile state.
 */
data class BoardTile(
    val id: Long,
    val type: ItemType,
    val special: SpecialType = SpecialType.NONE,
    val row: Int,
    val col: Int,
    val hasMoss: Boolean = false,
    val isClearing: Boolean = false,
    val isHighlighted: Boolean = false
)

/**
 * Coordinates on the grid.
 */
data class Position(val row: Int, val col: Int)

/**
 * Biomes in the enchanted forest realm.
 */
enum class Biome(val title: String, val subtitle: String, val primaryColor: Color) {
    WHISPERING_GROVE("Whispering Grove", "Gentle glades with singing brook crystals", EmeraldGem),
    LUMINESCENT_HOLLOW("Luminescent Hollow", "Bioluminescent flora and mossy stones", ForestEmeraldLight),
    MOONLIT_SANCTUARY("Moonlit Sanctuary", "Silvery night grove of ancient coins", SapphireGem),
    ELDER_TREE_HEART("Elder Tree Heart", "The mythical core pulsing with raw primal magic", ForestGold),
    CELESTIAL_CANOPY("Celestial Grape Arbor", "Where the mythical grapevine climbs into starry eternity", AmethystGem)
}

/**
 * Level goals and requirements.
 */
data class LevelConfig(
    val levelNumber: Int,
    val name: String,
    val biome: Biome,
    val maxMoves: Int,
    val targetScore: Int,
    val targetCoins: Int = 0,
    val targetMoss: Int = 0,
    val oneStarScore: Int,
    val twoStarScore: Int,
    val threeStarScore: Int,
    val mossPattern: List<Position> = emptyList()
)

/**
 * Saved completion record for a level.
 */
data class LevelRecord(
    val levelNumber: Int,
    val stars: Int,
    val highScore: Int,
    val isUnlocked: Boolean
)

/**
 * Swipe directions for moving gems and coins.
 */
enum class SwipeDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

/**
 * Dynamic visual effects rendered on the game board canvas.
 */
sealed class BoardVisualEffect(open val id: Long) {
    data class RowSliderFlash(
        override val id: Long,
        val row: Int,
        val durationMs: Int = 400
    ) : BoardVisualEffect(id)

    data class ColSliderFlash(
        override val id: Long,
        val col: Int,
        val durationMs: Int = 400
    ) : BoardVisualEffect(id)

    data class ChainStackLightBurst(
        override val id: Long,
        val row: Int,
        val col: Int,
        val isCoinStack: Boolean,
        val durationMs: Int = 500
    ) : BoardVisualEffect(id)

    data class FullCanvasTreasureWipe(
        override val id: Long,
        val originRow: Int,
        val originCol: Int,
        val durationMs: Int = 850
    ) : BoardVisualEffect(id)
}

