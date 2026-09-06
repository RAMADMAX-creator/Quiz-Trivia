package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class MazeDifficulty(val label: String, val size: Int, val badge: String) {
    EASY("Easy", 5, "🟢 5x5"),
    MEDIUM("Medium", 6, "🟡 6x6"),
    HARD("Hard", 8, "🔴 8x8")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf(MazeDifficulty.MEDIUM) }
    var showExportDialog by remember { mutableStateOf(false) }
    var pageTrimSize by remember { mutableStateOf("8.5\" x 11\" (Worksheet)") }
    var includeAnswerKey by remember { mutableStateOf(true) }
    var isPdfExported by remember { mutableStateOf(false) }

    val mazeSize = selectedDifficulty.size
    var playerX by remember { mutableIntStateOf(0) }
    var playerY by remember { mutableIntStateOf(0) }
    val targetX = mazeSize - 1
    val targetY = mazeSize - 1

    var stepCount by remember { mutableIntStateOf(0) }
    var isWon by remember { mutableStateOf(false) }
    var levelNumber by remember { mutableIntStateOf(1) }

    // Predefined walls set based on maze size and level
    val walls = remember(selectedDifficulty, levelNumber) {
        when (selectedDifficulty) {
            MazeDifficulty.EASY -> setOf(
                Pair(1, 0), Pair(1, 1), Pair(1, 2),
                Pair(3, 1), Pair(3, 2), Pair(3, 3), Pair(2, 4)
            )
            MazeDifficulty.MEDIUM -> when (levelNumber % 3) {
                1 -> setOf(
                    Pair(1, 0), Pair(1, 1), Pair(1, 2),
                    Pair(3, 1), Pair(3, 2), Pair(3, 3), Pair(3, 4),
                    Pair(4, 3), Pair(0, 4), Pair(1, 4)
                )
                2 -> setOf(
                    Pair(0, 1), Pair(2, 1), Pair(3, 1), Pair(4, 1),
                    Pair(1, 3), Pair(2, 3), Pair(4, 3), Pair(5, 3),
                    Pair(3, 4), Pair(3, 5)
                )
                else -> setOf(
                    Pair(1, 1), Pair(2, 1), Pair(4, 1),
                    Pair(1, 3), Pair(3, 3), Pair(4, 3),
                    Pair(0, 4), Pair(2, 4), Pair(5, 4)
                )
            }
            MazeDifficulty.HARD -> setOf(
                Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(1, 3), Pair(1, 5),
                Pair(3, 1), Pair(3, 2), Pair(3, 3), Pair(3, 5), Pair(3, 6),
                Pair(5, 0), Pair(5, 1), Pair(5, 3), Pair(5, 4), Pair(5, 5),
                Pair(6, 3), Pair(0, 6), Pair(2, 6), Pair(4, 6), Pair(6, 6)
            )
        }
    }

    val pathTrail = remember(selectedDifficulty, levelNumber) { mutableStateListOf(Pair(0, 0)) }

    fun movePlayer(dx: Int, dy: Int) {
        if (isWon) return
        val newX = playerX + dx
        val newY = playerY + dy

        if (newX in 0 until mazeSize && newY in 0 until mazeSize) {
            if (!walls.contains(Pair(newX, newY))) {
                playerX = newX
                playerY = newY
                stepCount++
                pathTrail.add(Pair(newX, newY))

                if (newX == targetX && newY == targetY) {
                    isWon = true
                }
            }
        }
    }

    fun restartMaze() {
        playerX = 0
        playerY = 0
        stepCount = 0
        isWon = false
        pathTrail.clear()
        pathTrail.add(Pair(0, 0))
    }

    fun nextLevel() {
        levelNumber++
        restartMaze()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("maze_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🌀 1000 Mazes Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Level $levelNumber • Steps: $stepCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.testTag("maze_export_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Export Worksheet", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                    onClick = { restartMaze() },
                    modifier = Modifier.testTag("maze_restart_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart")
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- Difficulty Selector Bar ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Maze Grid:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MazeDifficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDifficulty = diff
                                restartMaze()
                            },
                            label = {
                                Text(
                                    text = "${diff.badge} ${diff.label}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("maze_diff_chip_${diff.name}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Instructions Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚀", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Guide Rocket to Star 🏆 (${selectedDifficulty.label})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { showExportDialog = true }
                ) {
                    Text(
                        text = "🖨️ Worksheet PDF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Maze Canvas Board ---
        Card(
            modifier = Modifier
                .size(300.dp)
                .testTag("interactive_maze_board"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6750A4))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val trailColor = Color(0xFFFFB4AB)
                val wallColor = Color(0xFF49454F)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellSize = size.width / mazeSize

                    // Draw Grid lines
                    for (i in 0..mazeSize) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(i * cellSize, 0f),
                            end = Offset(i * cellSize, size.height),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, i * cellSize),
                            end = Offset(size.width, i * cellSize),
                            strokeWidth = 1f
                        )
                    }

                    // Draw Walls
                    walls.forEach { (wx, wy) ->
                        drawRect(
                            color = wallColor,
                            topLeft = Offset(wx * cellSize + 2f, wy * cellSize + 2f),
                            size = Size(cellSize - 4f, cellSize - 4f)
                        )
                    }

                    // Draw Trail
                    pathTrail.forEach { (tx, ty) ->
                        drawCircle(
                            color = trailColor,
                            center = Offset(tx * cellSize + cellSize / 2f, ty * cellSize + cellSize / 2f),
                            radius = cellSize / 6f
                        )
                    }
                }

                // Grid Items Overlay
                Column(modifier = Modifier.fillMaxSize()) {
                    for (r in 0 until mazeSize) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0 until mazeSize) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val iconSize = if (selectedDifficulty == MazeDifficulty.HARD) 16.sp else 22.sp
                                    when {
                                        r == playerY && c == playerX -> {
                                            Text("🚀", fontSize = iconSize)
                                        }
                                        r == targetY && c == targetX -> {
                                            Text("🏆", fontSize = iconSize)
                                        }
                                        walls.contains(Pair(c, r)) -> {
                                            Text("🧱", fontSize = if (selectedDifficulty == MazeDifficulty.HARD) 12.sp else 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Victory Banner ---
        AnimatedVisibility(
            visible = isWon,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("maze_victory_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6750A4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 MAZE COMPLETED (${selectedDifficulty.label})!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6750A4)
                    )
                    Text(
                        text = "Solved in $stepCount steps! You earned +50 XP 🌟",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF49454F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { nextLevel() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next Level 🚀", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4458)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Print Worksheet 🖨️", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- D-Pad Directional Controller ---
        if (!isWon) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.testTag("maze_dpad_controller")
            ) {
                // UP
                IconButton(
                    onClick = { movePlayer(0, -1) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("dpad_up")
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LEFT
                    IconButton(
                        onClick = { movePlayer(-1, 0) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("dpad_left")
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                    }

                    // RIGHT
                    IconButton(
                        onClick = { movePlayer(1, 0) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("dpad_right")
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                    }
                }

                // DOWN
                IconButton(
                    onClick = { movePlayer(0, 1) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("dpad_down")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                }
            }
        }
    }

    // --- Export Modal Dialog ---
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = {
                showExportDialog = false
                isPdfExported = false
            },
            icon = { Text("🖨️", fontSize = 36.sp) },
            title = {
                Text(
                    text = "Printable Maze Worksheet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Export this ${selectedDifficulty.label} (${selectedDifficulty.badge}) Maze Level $levelNumber as a high-quality printable PDF activity sheet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Paper Size:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("8.5\" x 11\" (Letter)", "A4 (Standard)").forEach { sizeOption ->
                            val isSelected = pageTrimSize == sizeOption
                            FilterChip(
                                selected = isSelected,
                                onClick = { pageTrimSize = sizeOption },
                                label = { Text(sizeOption, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = includeAnswerKey,
                            onCheckedChange = { includeAnswerKey = it }
                        )
                        Text(text = "Include Maze Path Solution Key", fontSize = 12.sp)
                    }

                    if (isPdfExported) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Printable Maze PDF generated successfully! Ready for printing or offline study.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isPdfExported = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isPdfExported) "Saved PDF" else "Export PDF")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExportDialog = false
                        isPdfExported = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

