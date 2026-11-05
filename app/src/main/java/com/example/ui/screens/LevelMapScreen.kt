package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Biome
import com.example.model.LevelRecord
import com.example.model.LevelRepository
import com.example.ui.components.ForestFireflies
import com.example.ui.theme.*

@Composable
fun LevelMapScreen(
    levelRecords: List<LevelRecord>,
    playerCoins: Int,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForestDarkBackground.copy(alpha = 0.88f))
        )

        ForestFireflies()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ForestSurfaceVariant)
                        .border(1.dp, ForestCardBorder, CircleShape)
                        .testTag("map_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Menu",
                        tint = StarWhite
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FOREST PATH",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForestGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "12 Enchanted Levels",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ForestSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$playerCoins",
                            style = MaterialTheme.typography.labelLarge,
                            color = ForestGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Biomes list with levels
            val biomes = listOf(
                Biome.WHISPERING_GROVE,
                Biome.LUMINESCENT_HOLLOW,
                Biome.MOONLIT_SANCTUARY,
                Biome.ELDER_TREE_HEART
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(biomes) { biome ->
                    BiomeSection(
                        biome = biome,
                        levelRecords = levelRecords,
                        onLevelSelected = onLevelSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun BiomeSection(
    biome: Biome,
    levelRecords: List<LevelRecord>,
    onLevelSelected: (Int) -> Unit
) {
    val levelsInBiome = LevelRepository.levels.filter { it.biome == biome }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ForestSurface.copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Biome Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(biome.primaryColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = biome.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = biome.primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = biome.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Levels grid in this biome
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                levelsInBiome.forEach { config ->
                    val record = levelRecords.find { it.levelNumber == config.levelNumber }
                    val isUnlocked = record?.isUnlocked ?: (config.levelNumber == 1)
                    val stars = record?.stars ?: 0

                    LevelNode(
                        levelNumber = config.levelNumber,
                        levelName = config.name,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        onClick = { if (isUnlocked) onLevelSelected(config.levelNumber) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelNode(
    levelNumber: Int,
    levelName: String,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(4.dp)
            .testTag("level_node_$levelNumber")
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) {
                        Brush.radialGradient(
                            colors = listOf(ForestSurfaceVariant, ForestEmeraldDark)
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1B2620), Color(0xFF0F1713))
                        )
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isUnlocked) ForestGold else Color.Gray.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$levelNumber",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Stars row
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (i in 1..3) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (i <= stars) ForestGold else Color.Gray.copy(alpha = 0.35f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Text(
            text = levelName.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isUnlocked) StarWhite else Color.Gray,
            fontSize = 9.sp
        )
    }
}
