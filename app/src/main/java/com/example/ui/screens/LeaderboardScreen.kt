package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.LeaderboardEntry
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    currentDivision: String,
    leaderboardEntries: List<LeaderboardEntry>,
    arenaMode: String,
    onSelectDivision: (String) -> Unit,
    onSelectArenaMode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val divisions = listOf(
        "KIDS" to "👶 Kids",
        "JUNIORS" to "🎒 Juniors",
        "SENIORS" to "🎓 Seniors"
    )

    var showAddFriendDialog by remember { mutableStateOf(false) }
    var friendCodeInput by remember { mutableStateOf("") }

    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text("Add Friend to League") },
            text = {
                Column {
                    Text("Enter your friend's 6-character Quiz Master Player ID to compete in private friend leaderboards:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = friendCodeInput,
                        onValueChange = { friendCodeInput = it.take(8).uppercase() },
                        label = { Text("Friend Code (e.g. QZ-8821)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddFriendDialog = false
                        friendCodeInput = ""
                    }
                ) {
                    Text("Add Friend")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val displayedEntries = if (arenaMode == "FRIENDS") {
        leaderboardEntries.filter { it.isFriend || it.isMe }
    } else {
        leaderboardEntries
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- Header Title ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = "Leaderboard",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Leaderboard Arena",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Weekly Season resets in 2d 14h",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(
                onClick = { showAddFriendDialog = true },
                modifier = Modifier.testTag("add_friend_button")
            ) {
                Icon(
                    imageVector = Icons.Default.GroupAdd,
                    contentDescription = "Add Friend",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Arena Mode Toggle (Global vs Friends)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { onSelectArenaMode("GLOBAL") },
                modifier = Modifier.weight(1f).height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (arenaMode == "GLOBAL") MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (arenaMode == "GLOBAL") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🌐 Global Arena", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = { onSelectArenaMode("FRIENDS") },
                modifier = Modifier.weight(1f).height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (arenaMode == "FRIENDS") MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (arenaMode == "FRIENDS") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("👥 Friends League", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Difficulty Division Tabs ---
        TabRow(
            selectedTabIndex = divisions.indexOfFirst { it.first == currentDivision }.coerceAtLeast(0),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .testTag("leaderboard_tabs")
        ) {
            divisions.forEach { (key, label) ->
                val isSelected = currentDivision == key
                Tab(
                    selected = isSelected,
                    onClick = { onSelectDivision(key) },
                    text = {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("tab_$key")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Leaderboard List ---
        if (displayedEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (arenaMode == "FRIENDS") "No friends added yet. Tap + to invite friends!" else "Loading players...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
                    .testTag("leaderboard_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(displayedEntries) { index, entry ->
                    LeaderboardRow(
                        rank = index + 1,
                        entry = entry
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier
) {
    val (rankIndicator, cardBorder, cardBackground) = when {
        entry.isMe -> Triple(
            null,
            BorderStroke(2.dp, VibrantPrimary),
            MaterialTheme.colorScheme.surfaceVariant
        )
        rank == 1 -> Triple(
            "🥇",
            BorderStroke(1.dp, AmberTertiary),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
        rank == 2 -> Triple(
            "🥈",
            BorderStroke(1.dp, Color(0xFF94A3B8)),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
        rank == 3 -> Triple(
            "🥉",
            BorderStroke(1.dp, Color(0xFFB45309)),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
        else -> Triple(
            null,
            null,
            MaterialTheme.colorScheme.surface
        )
    }

    val avatarBgColor = when(entry.avatarSeed % 5) {
        0 -> VibrantPrimary
        1 -> VibrantSecondary
        2 -> Color(0xFF10B981)
        3 -> Color(0xFFEC4899)
        else -> AmberTertiary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (entry.isMe) "leaderboard_row_me" else "leaderboard_row_$rank"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Indicator
            Box(
                modifier = Modifier.width(38.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (rankIndicator != null) {
                    Text(text = rankIndicator, fontSize = 22.sp)
                } else {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (entry.isMe) VibrantPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (entry.isMe) Icons.Default.Star else Icons.Default.Person,
                    contentDescription = "Player Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Username and League
            Column(modifier = Modifier.weight(1.0f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (entry.isMe) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (entry.isMe) VibrantPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (entry.isFriend) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Friend",
                            style = MaterialTheme.typography.labelSmall,
                            color = VibrantSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = "${entry.league} Tier",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Score XP
            Text(
                text = "${entry.score} XP",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (entry.isMe) VibrantPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
        }
    }
}

