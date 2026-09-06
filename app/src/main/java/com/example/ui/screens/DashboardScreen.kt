package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.UserProgress
import com.example.ui.theme.*
import com.example.ui.viewmodel.TimerMode

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll

@Composable
fun DashboardScreen(
    userProgress: UserProgress?,
    selectedTimerMode: TimerMode,
    onTimerModeSelected: (TimerMode) -> Unit,
    onStartQuiz: (category: String) -> Unit,
    onStartGameMode: (mode: com.example.ui.viewmodel.GameMode, category: String) -> Unit = { _, cat -> onStartQuiz(cat) },
    onUpdateAgeDivision: (String) -> Unit,
    onOpenMaze: () -> Unit = {},
    onOpenWordSearch: () -> Unit = {},
    onOpenSudoku: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val username = userProgress?.username ?: "Explorer"
    val ageDivision = userProgress?.ageDivision ?: "KIDS"
    val streak = userProgress?.streakCount ?: 0
    val totalXp = userProgress?.totalXp ?: 0
    
    // Level math: Level 1 starts at 0 XP, each level requires 200 XP
    val currentLevel = 1 + (totalXp / 200)
    val xpInCurrentLevel = totalXp % 200
    val progressToNextLevel = xpInCurrentLevel.toFloat() / 200f

    val initials = remember(username) {
        username.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .let { if (it.isEmpty()) "EX" else it }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // --- 1. Top App Bar ---
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0BCFF))
                        .border(2.dp, Color(0xFFEADDFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color(0xFF21005D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                // Welcome Text
                Column {
                    Text(
                        text = "WELCOME BACK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            // Streak Flame Badge (styled directly from the HTML)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFFD8E4))
                    .border(1.dp, Color(0xFFF9DEDC), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("streak_badge"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "$streak Day Streak",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF31111D)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 2. Difficulty / Age Selection Chips (inline navigation) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val levels = listOf(
                "KIDS" to "Kids (Age 5-8)",
                "JUNIORS" to "Teens (9-12)",
                "SENIORS" to "Masters (13+)"
            )
            levels.forEach { (key, label) ->
                val isSelected = ageDivision == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color(0xFF6750A4) else Color(0xFFEADDFF))
                        .clickable { onUpdateAgeDivision(key) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF21005D)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3. Hero Level Board (Leaderboard Preview style) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("level_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF21005D))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Background generated hero banner cropped nicely inside
                Image(
                    painter = painterResource(id = R.drawable.img_quiz_hero),
                    contentDescription = "Quiz Quest Theme Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = 0.15f
                )
                
                // Content Overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "LEVEL $currentLevel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "$totalXp Total XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEADDFF)
                            )
                        }
                        
                        // Global Rank Indicator style from HTML
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Global Rank",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "#${(42 + (totalXp / 50) % 100)}", // Dynamic aesthetic rank
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD0BCFF)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Progress Bar styled from the HTML track
                    LinearProgressIndicator(
                        progress = { progressToNextLevel },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFD0BCFF),
                        trackColor = Color(0xFF4F378B)
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Top 1% this week",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEADDFF)
                        )
                        Text(
                            text = "${(progressToNextLevel * 100).toInt()}% to Level ${currentLevel + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEADDFF)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Daily Streak & Puzzle Completion Progress Card ---
        val completedDailyPuzzles = 3
        val totalDailyPuzzles = 5
        val puzzleProgressFraction = completedDailyPuzzles.toFloat() / totalDailyPuzzles.toFloat()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("daily_streak_puzzle_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            border = BorderStroke(1.5.dp, Color(0xFFFFB300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Header: Flame + Streak Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF6D00).copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🔥", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${streak.coerceAtLeast(3)} Days Active!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3E2723),
                                modifier = Modifier.testTag("streak_count_display")
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFF6D00)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "⚡ 1.5x XP Boost",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weekly Day Bubbles Row (Mon - Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("M" to true, "T" to true, "W" to true, "T" to false, "F" to false, "S" to false, "S" to false)
                    days.forEachIndexed { index, (dayLabel, isDone) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) Color(0xFFFF6D00) else Color(0xFFFFECB3))
                                    .border(
                                        1.dp,
                                        if (isDone) Color(0xFFE65100) else Color(0xFFFFD54F),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDone) "🔥" else dayLabel,
                                    fontSize = if (isDone) 14.sp else 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) Color.White else Color(0xFF8D6E63)
                                )
                            }
                            Text(
                                text = dayLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFFFE082).copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Daily Puzzle Completion Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧩", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Puzzle Goal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    }
                    Text(
                        text = "$completedDailyPuzzles / $totalDailyPuzzles Completed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE65100)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Visual Progress Bar
                LinearProgressIndicator(
                    progress = { puzzleProgressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .testTag("puzzle_progress_bar"),
                    color = Color(0xFFFF6D00),
                    trackColor = Color(0xFFFFECB3)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5 Puzzle Activity Slots Checkboxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val puzzleTypes = listOf(
                        "Maze" to "🌀",
                        "Word" to "🔤",
                        "Math" to "🧮",
                        "Sci" to "🔬",
                        "GK" to "🌍"
                    )

                    puzzleTypes.forEachIndexed { index, (label, emoji) ->
                        val isCompleted = index < completedDailyPuzzles
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCompleted) Color(0xFFFFF3E0) else Color(0xFFFFFFFF),
                            border = BorderStroke(
                                1.dp,
                                if (isCompleted) Color(0xFFFFB74D) else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .testTag("daily_puzzle_slot_$index")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = if (isCompleted) "✅" else emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color(0xFFE65100) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Global Activity Difficulty Level Selector ---
        var dashboardDifficulty by remember { mutableStateOf("Medium") }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_difficulty_selector"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Puzzle Complexity Level",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when (dashboardDifficulty) {
                            "Easy" -> Color(0xFF2E7D32)
                            "Medium" -> Color(0xFFE65100)
                            else -> Color(0xFFC62828)
                        }
                    ) {
                        Text(
                            text = "$dashboardDifficulty Mode Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("Easy", "🟢 5x5 Grid", "Ages 4–6"),
                        Triple("Medium", "🟡 6x6 Grid", "Ages 6–8"),
                        Triple("Hard", "🔴 8x8 Grid", "Ages 8–12")
                    ).forEach { (diff, gridText, ageText) ->
                        val isSelected = dashboardDifficulty == diff
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { dashboardDifficulty = diff }
                                .testTag("diff_chip_$diff"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = diff,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = gridText,
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Interactive Activity Games Row (Mazes & Word Search) ---
        Text(
            text = "Interactive Activity Games",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. 1000 Mazes Challenge Game
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenMaze() }
                    .testTag("btn_interactive_mazes"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6750A4)),
                border = BorderStroke(1.dp, Color(0xFFD0BCFF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("🌀", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1000 Mazes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Canvas Game",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // 2. Word Search Challenge Game
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenWordSearch() }
                    .testTag("btn_word_search"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF006874)),
                border = BorderStroke(1.dp, Color(0xFF80DEEA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("🔤", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Word Search",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Grid Vocab",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // 3. 4x4 Sudoku Kids Game
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenSudoku() }
                    .testTag("btn_sudoku_game"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF984061)),
                border = BorderStroke(1.dp, Color(0xFFF8BBD0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("🧩", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "4x4 Sudoku",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Logic Puzzle",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Timer Prep Mode Options ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("prep_timer_card"),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFEADDFF)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 20.sp
                    )
                    Column {
                        Text(
                            text = "PREP TIMER MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = when(selectedTimerMode) {
                                TimerMode.PRACTICE -> "Relaxed learning mode with explanations"
                                TimerMode.SAT -> "SAT Exam simulation (60s limit per question)"
                                TimerMode.NTS -> "NTS Rapid-fire simulation (30s limit per question)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF49454F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimerMode.values().forEach { mode ->
                        val isSelected = selectedTimerMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color(0xFF6750A4) else Color(0xFFEADDFF).copy(alpha = 0.5f))
                                .clickable { onTimerModeSelected(mode) }
                                .padding(vertical = 10.dp)
                                .testTag("timer_mode_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when(mode) {
                                        TimerMode.PRACTICE -> "Practice"
                                        TimerMode.SAT -> "SAT Mode"
                                        TimerMode.NTS -> "NTS Mode"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF21005D)
                                )
                                Text(
                                    text = when(mode) {
                                        TimerMode.PRACTICE -> "Untimed"
                                        TimerMode.SAT -> "60s / Q"
                                        TimerMode.NTS -> "30s / Q"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF21005D).copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Game Modes Showcase Row ---
        Text(
            text = "Gamified Arena Modes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mode 1: Speed Blitz
            ModeHeroCard(
                title = "Speed Blitz",
                subtitle = "60s Fast Round",
                badge = "⚡ 2x XP",
                iconEmoji = "⚡",
                bgGradient = listOf(Color(0xFF6750A4), Color(0xFF7D5260)),
                onClick = { onStartGameMode(com.example.ui.viewmodel.GameMode.SPEED_BLITZ, "GK") }
            )

            // Mode 2: Sudden Death Survival
            ModeHeroCard(
                title = "Survival",
                subtitle = "3 Lives Only",
                badge = "❤️ High Stakes",
                iconEmoji = "❤️",
                bgGradient = listOf(Color(0xFFB3261E), Color(0xFF8C1D18)),
                onClick = { onStartGameMode(com.example.ui.viewmodel.GameMode.SURVIVAL, "SCIENCE") }
            )

            // Mode 3: Daily Challenge
            ModeHeroCard(
                title = "Daily Challenge",
                subtitle = "Curated 5 Questions",
                badge = "🌟 +50 Coins",
                iconEmoji = "🌟",
                bgGradient = listOf(Color(0xFFE65100), Color(0xFFFF8F00)),
                onClick = { onStartGameMode(com.example.ui.viewmodel.GameMode.DAILY_CHALLENGE, "COUNTRIES") }
            )

            // Mode 4: SAT Exam Prep
            ModeHeroCard(
                title = "SAT Practice",
                subtitle = "60s Per Question",
                badge = "🎓 Timed",
                iconEmoji = "🎓",
                bgGradient = listOf(Color(0xFF0061A4), Color(0xFF00487D)),
                onClick = { onStartGameMode(com.example.ui.viewmodel.GameMode.SAT_PREP, "MATHS") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        var selectedSubject by remember { mutableStateOf("ALL") }

        Text(
            text = "Subject Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // --- Category Selector Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
                .testTag("category_selector_bar"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subjects = listOf(
                "ALL" to "✨ All",
                "MATH" to "🧮 Math",
                "SCIENCE" to "🔬 Science",
                "GK" to "💡 General Knowledge",
                "GEOGRAPHY" to "🌍 Geography",
                "LOGIC" to "🧩 Logic Puzzles"
            )

            subjects.forEach { (key, label) ->
                val isSelected = selectedSubject == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSubject = key },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("category_chip_$key")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3. Categories Grid ---
        val allCategories = listOf(
            CategoryItem("COUNTRIES", "Geography", Icons.Default.Public, IndigoPrimary, "Flags, geography & culture", subjectGroup = "GEOGRAPHY"),
            CategoryItem("SCIENCE", "Science", Icons.Default.Science, Color(0xFF10B981), "Space, nature & chemistry", subjectGroup = "SCIENCE"),
            CategoryItem("HISTORY", "History", Icons.Default.AutoStories, AmberTertiary, "Civilizations & major events", subjectGroup = "LOGIC"),
            CategoryItem("MATHS", "Math", Icons.Default.Calculate, Color(0xFFEC4899), "Numbers, puzzles & logic", subjectGroup = "MATH"),
            CategoryItem("GK", "General Knowledge", Icons.Default.Lightbulb, RoyalSecondary, "Facts & world trivia", subjectGroup = "GK")
        )

        val filteredCategories = remember(selectedSubject) {
            when (selectedSubject) {
                "ALL" -> allCategories
                "MATH" -> allCategories.filter { it.subjectGroup == "MATH" }
                "SCIENCE" -> allCategories.filter { it.subjectGroup == "SCIENCE" }
                "GK" -> allCategories.filter { it.subjectGroup == "GK" }
                "GEOGRAPHY" -> allCategories.filter { it.subjectGroup == "GEOGRAPHY" }
                "LOGIC" -> allCategories.filter { it.subjectGroup == "LOGIC" || it.subjectGroup == "MATH" }
                else -> allCategories
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("categories_grid"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredCategories.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        CategoryCard(
                            item = item,
                            onClick = { onStartQuiz(item.key) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class CategoryItem(
    val key: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val subtitle: String,
    val subjectGroup: String = "ALL"
)

@Composable
fun CategoryCard(
    item: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamically retrieve the background and border colors for the Vibrant Palette
    val (bgColor, borderColor) = when (item.key) {
        "SCIENCE" -> ScienceBg to ScienceBorder
        "HISTORY" -> HistoryBg to HistoryBorder
        "COUNTRIES" -> CountriesBg to CountriesBorder
        "MATHS" -> MathsBg to MathsBorder
        else -> GkBg to GkBorder
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)
            .clickable(onClick = onClick)
            .testTag("category_card_${item.key.lowercase()}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // White rounded container for the category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Determine icon text/emoji from the category key to make it extra vibrant!
                val emoji = when (item.key) {
                    "SCIENCE" -> "🔬"
                    "HISTORY" -> "🏛️"
                    "COUNTRIES" -> "🌍"
                    "MATHS" -> "📐"
                    else -> "💡"
                }
                Text(text = emoji, fontSize = 20.sp)
            }
            
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1B20) // Exact text color from the spec
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = Color(0xFF49454F), // Muted dark gray from spec
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ModeHeroCard(
    title: String,
    subtitle: String,
    badge: String,
    iconEmoji: String,
    bgGradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("mode_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgGradient.first())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(bgGradient))
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = iconEmoji, fontSize = 22.sp)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

