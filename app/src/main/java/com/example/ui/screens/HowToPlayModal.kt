package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun HowToPlayModal(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, ForestGold, RoundedCornerShape(24.dp))
                .testTag("how_to_play_dialog"),
            color = ForestDarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOW TO PLAY",
                        style = MaterialTheme.typography.titleLarge,
                        color = ForestGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_coins_and_gems),
                            contentDescription = "Magical Coins and Gems",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }

                    item {
                        GuideSection(
                            title = "🌿 Matching Gems & Coins",
                            text = "Swap adjacent tiles to line up 3 or more of the same gem or coin. Matches clear tiles and trigger cascading gravity from above!"
                        )
                    }

                    item {
                        GuideSection(
                            title = "✨ Special Power Matches",
                            text = "• Match 4 in a line: Creates a Nature Ray (Row or Column Blaster)!\n" +
                                   "• Match 5 in T or L shape: Creates a Spore Bomb (detonates a 3x3 area)!\n" +
                                   "• Match 5 in a straight line: Creates a Prismatic Tree Star! Swap it with any tile to clear every gem or coin of that type!"
                        )
                    }

                    item {
                        GuideSection(
                            title = "⚡ Mythical Combos",
                            text = "Swap two special tiles together for epic board-clearing magic:\n" +
                                   "• Star + Star: Wipes the entire board!\n" +
                                   "• Bomb + Blaster: Mega 3-row & 3-column cross blast!\n" +
                                   "• Blaster + Blaster: Simultaneous Row & Column cross!"
                        )
                    }

                    item {
                        GuideSection(
                            title = "🧚 Unique Forest Power-ups",
                            text = "• Pixie Wand (✨): Shatters any chosen tile without using a move.\n" +
                                   "• Root Swap (🌿): Forces any two adjacent tiles to swap without move penalty.\n" +
                                   "• Forest Breeze (🍃): Whirlwind shuffles the entire board.\n" +
                                   "• Sunbeam Strike (☀️): Blasts an entire row and column.\n" +
                                   "• Nectar Bloom (🌸): Instantly transforms a tile into a Prismatic Star!"
                        )
                    }

                    item {
                        GuideSection(
                            title = "🎯 Level Objectives & Moss",
                            text = "Reach the target score, clear creeping enchanted moss under tiles by matching over them, and collect ancient gold coins before you run out of moves!"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_how_to_play_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestEmeraldDark),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestEmeraldLight)
                ) {
                    Text(
                        text = "GOT IT, LET'S PLAY!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideSection(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ForestSurfaceVariant.copy(alpha = 0.8f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = ForestEmeraldLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = StarWhite,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
