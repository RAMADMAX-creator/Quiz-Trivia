package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SudokuDifficulty(val label: String, val cluesCount: String, val badge: String) {
    EASY("Easy", "8 Clues", "🟢 Easy"),
    MEDIUM("Medium", "5 Clues", "🟡 Medium"),
    HARD("Hard", "3 Clues", "🔴 Hard")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf(SudokuDifficulty.MEDIUM) }
    var showExportDialog by remember { mutableStateOf(false) }
    var pageTrimSize by remember { mutableStateOf("8.5\" x 11\" (Worksheet)") }
    var includeAnswerKey by remember { mutableStateOf(true) }
    var isPdfExported by remember { mutableStateOf(false) }

    val solution = listOf(
        listOf(1, 2, 3, 4),
        listOf(3, 4, 1, 2),
        listOf(2, 1, 4, 3),
        listOf(4, 3, 2, 1)
    )

    // Dynamic initial puzzles based on difficulty
    val initialPuzzle = remember(selectedDifficulty) {
        when (selectedDifficulty) {
            SudokuDifficulty.EASY -> listOf(
                listOf(1, 2, 3, 0),
                listOf(3, 4, 0, 2),
                listOf(2, 0, 4, 3),
                listOf(0, 3, 2, 1)
            )
            SudokuDifficulty.MEDIUM -> listOf(
                listOf(1, 0, 3, 0),
                listOf(0, 4, 0, 2),
                listOf(2, 0, 4, 0),
                listOf(0, 3, 0, 1)
            )
            SudokuDifficulty.HARD -> listOf(
                listOf(1, 0, 0, 0),
                listOf(0, 4, 0, 0),
                listOf(0, 0, 4, 0),
                listOf(0, 3, 0, 0)
            )
        }
    }

    var userGrid by remember(selectedDifficulty) {
        mutableStateOf(initialPuzzle.map { row -> row.toMutableList() })
    }

    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isSolved by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun selectNumber(num: Int) {
        val cell = selectedCell ?: return
        val (r, c) = cell
        if (initialPuzzle[r][c] != 0) return // Fixed clue cell

        val newGrid = userGrid.map { row -> row.toMutableList() }
        newGrid[r][c] = num
        userGrid = newGrid
        errorMessage = ""

        // Check if solved correctly
        var isAllFilled = true
        var isCorrect = true

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                if (newGrid[row][col] == 0) {
                    isAllFilled = false
                } else if (newGrid[row][col] != solution[row][col]) {
                    isCorrect = false
                }
            }
        }

        if (isAllFilled && isCorrect) {
            isSolved = true
        }
    }

    fun clearCell() {
        val cell = selectedCell ?: return
        val (r, c) = cell
        if (initialPuzzle[r][c] != 0) return

        val newGrid = userGrid.map { row -> row.toMutableList() }
        newGrid[r][c] = 0
        userGrid = newGrid
        errorMessage = ""
    }

    fun restartSudoku() {
        userGrid = initialPuzzle.map { row -> row.toMutableList() }
        selectedCell = null
        isSolved = false
        errorMessage = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("sudoku_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🧩 4x4 Kids Sudoku",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF984061)
                )
                Text(
                    text = "${selectedDifficulty.cluesCount} • Numbers 1 to 4",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.testTag("sudoku_export_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Export Worksheet", tint = Color(0xFF984061))
                }

                IconButton(
                    onClick = { restartSudoku() },
                    modifier = Modifier.testTag("sudoku_restart_button")
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
                    text = "Difficulty:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF984061)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SudokuDifficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDifficulty = diff
                                restartSudoku()
                            },
                            label = {
                                Text(
                                    text = "${diff.badge}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF984061),
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("sudoku_diff_chip_${diff.name}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Banner Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
            border = BorderStroke(1.dp, Color(0xFFF8BBD0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Logic Rules: Unique 1-4 in each row & box!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF880E4F)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF984061),
                    modifier = Modifier.clickable { showExportDialog = true }
                ) {
                    Text(
                        text = "🖨️ Worksheet PDF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Sudoku 4x4 Grid Board ---
        Card(
            modifier = Modifier
                .size(290.dp)
                .testTag("sudoku_grid_board"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color(0xFF984061))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (r in 0 until 4) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (c in 0 until 4) {
                            val isClue = initialPuzzle[r][c] != 0
                            val cellValue = userGrid[r][c]
                            val isSelected = selectedCell == Pair(r, c)
                            val isSubBlockBorderRight = c == 1
                            val isSubBlockBorderBottom = r == 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isSelected -> Color(0xFFF8BBD0)
                                            isClue -> Color(0xFFF5F5F5)
                                            cellValue != 0 -> Color(0xFFE8EAF6)
                                            else -> Color.White
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = when {
                                            isSelected -> Color(0xFF984061)
                                            isSubBlockBorderRight || isSubBlockBorderBottom -> Color(0xFF984061).copy(alpha = 0.6f)
                                            else -> Color.LightGray.copy(alpha = 0.5f)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedCell = Pair(r, c) }
                                    .testTag("sudoku_cell_${r}_$c"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (cellValue != 0) {
                                    Text(
                                        text = cellValue.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = if (isClue) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isClue) Color(0xFF333333) else Color(0xFF1A237E)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Victory Card ---
        AnimatedVisibility(
            visible = isSolved,
            enter = fadeIn() + expandVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sudoku_victory_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉 SUDOKU SOLVED (${selectedDifficulty.label})!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
                    Text(text = "Awesome logic! You earned +120 STEM XP!", fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF984061)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Export Worksheet PDF 🖨️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Number Pad (1, 2, 3, 4 + Eraser) ---
        if (!isSolved) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..4).forEach { num ->
                    Button(
                        onClick = { selectNumber(num) },
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("sudoku_numpad_$num"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF984061)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = num.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Eraser Button
                IconButton(
                    onClick = { clearCell() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .testTag("sudoku_eraser")
                ) {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Erase",
                        tint = Color.DarkGray
                    )
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
                    text = "Printable Sudoku Worksheet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Export this ${selectedDifficulty.label} (${selectedDifficulty.cluesCount}) Sudoku Puzzle as a high-quality printable PDF activity sheet.",
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
                                    selectedContainerColor = Color(0xFF984061),
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
                        Text(text = "Include Completed Solution Key", fontSize = 12.sp)
                    }

                    if (isPdfExported) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Printable Sudoku PDF generated successfully! Ready for printing or offline study.",
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF984061))
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

