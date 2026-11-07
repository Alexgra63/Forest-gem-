package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.PowerUpType
import com.example.ui.components.ForestFireflies
import com.example.ui.theme.*

@Composable
fun ShopScreen(
    playerCoins: Int,
    powerUpCounts: Map<PowerUpType, Int>,
    onBuyPowerUp: (PowerUpType) -> Unit,
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
                .background(ForestDarkBackground.copy(alpha = 0.90f))
        )

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
                        .testTag("shop_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = StarWhite
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FOREST TREASURY",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForestGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Exchange Coins for Mythical Powers",
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_forest_gold_coin),
                            contentDescription = "Gold Coin",
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$playerCoins",
                            style = MaterialTheme.typography.titleMedium,
                            color = ForestGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Power-ups Catalog
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(PowerUpType.entries) { powerUp ->
                    val owned = powerUpCounts[powerUp] ?: 0
                    val canAfford = playerCoins >= powerUp.coinCost

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_item_${powerUp.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ForestSurface.copy(alpha = 0.85f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canAfford) ForestCardBorder else ForestCardBorder.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Icon + Info
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(ForestSurfaceVariant, ForestEmeraldDark)
                                            )
                                        )
                                        .border(1.5.dp, ForestGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = powerUp.iconSymbol, fontSize = 24.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = powerUp.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = StarWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ForestEmeraldDark.copy(alpha = 0.5f)
                                        ) {
                                            Text(
                                                text = "Owned: $owned",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ForestEmeraldLight,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = powerUp.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Buy Button
                            Button(
                                onClick = { onBuyPowerUp(powerUp) },
                                enabled = canAfford,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGoldDark,
                                    disabledContainerColor = Color(0xFF26332C)
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_forest_gold_coin),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${powerUp.coinCost}",
                                        color = if (canAfford) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
