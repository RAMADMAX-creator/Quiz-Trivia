package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
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
import com.example.ui.theme.*

@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    category: String,
    ageDivision: String,
    streak: Int,
    onBackHome: () -> Unit,
    onViewLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalQuestions > 0) (score.toFloat() / totalQuestions.toFloat() * 100).toInt() else 0
    
    val (title, iconText, message) = when {
        percentage == 100 -> Triple("Flawless Mastery!", "🏆", "Incredible! You answered every single question correctly!")
        percentage >= 70 -> Triple("Outstanding!", "🌟", "Great job! You have demonstrated excellent subject mastery.")
        percentage >= 40 -> Triple("Solid Effort!", "👍", "Good progress! Practice again to climb the global leaderboard.")
        else -> Triple("Keep Practicing!", "📚", "Every mistake is a learning step. Review the rationale and retry!")
    }

    val xpPerCorrect = 15
    val completionXp = 25
    val totalXpGained = (score * xpPerCorrect) + completionXp
    val coinsGained = (score * 5) + (if (percentage == 100) 25 else 5)
    val gemsGained = if (percentage == 100) 1 else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = iconText,
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Score Statistics Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("result_stats_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$score / $totalQuestions Correct",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Rewards breakdown (XP, Coins, Gems)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RewardItem(label = "XP Earned", value = "+$totalXpGained XP", icon = "⚡", highlight = true)
                    RewardItem(label = "Coins Won", value = "+$coinsGained", icon = "🪙")
                    if (gemsGained > 0) {
                        RewardItem(label = "Bonus Gem", value = "+$gemsGained 💎", icon = "💎")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Streak Celebrator Banner ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("streak_celebration_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AmberTertiary.copy(alpha = 0.15f)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔥", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Active Streak: $streak Days!",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = AmberTertiary
                    )
                    Text(
                        text = "Daily learning multiplies your XP and earns bonus streak chests.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Action Control Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBackHome,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("result_home_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Arena Home",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onViewLeaderboard,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("result_leaderboard_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = "Leaderboard",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Leaderboard",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun RewardItem(
    label: String,
    value: String,
    icon: String = "",
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = if (highlight) 16.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) AmberTertiary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

