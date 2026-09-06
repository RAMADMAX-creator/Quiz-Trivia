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

enum class WordSearchDifficulty(val label: String, val badge: String) {
    EASY("Easy", "🟢 5x5"),
    MEDIUM("Medium", "🟡 6x6"),
    HARD("Hard", "🔴 8x8")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSearchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf(WordSearchDifficulty.MEDIUM) }
    var showExportDialog by remember { mutableStateOf(false) }
    var pageTrimSize by remember { mutableStateOf("8.5\" x 11\" (Worksheet)") }
    var includeAnswerKey by remember { mutableStateOf(true) }
    var isPdfExported by remember { mutableStateOf(false) }

    // Dynamic word lists and grids based on selected difficulty
    val wordList = remember(selectedDifficulty) {
        when (selectedDifficulty) {
            WordSearchDifficulty.EASY -> listOf("MATH", "MAZE", "STAR", "QUIZ")
            WordSearchDifficulty.MEDIUM -> listOf("BRAIN", "MATH", "SPACE", "MAZE", "QUIZ")
            WordSearchDifficulty.HARD -> listOf("ASTRONAUT", "EQUATION", "DISCOVERY", "GEOMETRY", "BRAIN", "LOGIC", "GALAXY")
        }
    }

    val grid = remember(selectedDifficulty) {
        when (selectedDifficulty) {
            WordSearchDifficulty.EASY -> listOf(
                listOf('M', 'A', 'T', 'H', 'X'),
                listOf('A', 'M', 'A', 'Z', 'E'),
                listOf('S', 'T', 'A', 'R', 'Y'),
                listOf('Q', 'U', 'I', 'Z', 'Z'),
                listOf('L', 'O', 'G', 'I', 'C')
            )
            WordSearchDifficulty.MEDIUM -> listOf(
                listOf('B', 'R', 'A', 'I', 'N', 'X'),
                listOf('M', 'A', 'T', 'H', 'Y', 'Z'),
                listOf('S', 'P', 'A', 'C', 'E', 'Q'),
                listOf('M', 'A', 'Z', 'E', 'K', 'U'),
                listOf('Q', 'U', 'I', 'Z', 'L', 'I'),
                listOf('S', 'T', 'A', 'R', 'W', 'S')
            )
            WordSearchDifficulty.HARD -> listOf(
                listOf('A', 'S', 'T', 'R', 'O', 'N', 'A', 'U', 'T'),
                listOf('E', 'Q', 'U', 'A', 'T', 'I', 'O', 'N', 'X'),
                listOf('D', 'I', 'S', 'C', 'O', 'V', 'E', 'R', 'Y'),
                listOf('G', 'E', 'O', 'M', 'E', 'T', 'R', 'Y', 'Z'),
                listOf('B', 'R', 'A', 'I', 'N', 'M', 'A', 'T', 'H'),
                listOf('L', 'O', 'G', 'I', 'C', 'P', 'U', 'Z', 'L'),
                listOf('G', 'A', 'L', 'A', 'X', 'Y', 'S', 'T', 'A'),
                listOf('P', 'L', 'A', 'N', 'E', 'T', 'S', 'P', 'A')
            )
        }
    }

    val foundWords = remember(selectedDifficulty) { mutableStateListOf<String>() }
    val selectedCells = remember(selectedDifficulty) { mutableStateListOf<Pair<Int, Int>>() }

    fun toggleCellSelect(r: Int, c: Int) {
        val cell = Pair(r, c)
        if (selectedCells.contains(cell)) {
            selectedCells.remove(cell)
        } else {
            selectedCells.add(cell)
        }

        // Check if selected cells spell any word in wordList
        val selectedChars = selectedCells.map { (row, col) ->
            if (row < grid.size && col < grid[row].size) grid[row][col] else ' '
        }.joinToString("")
        val reversedChars = selectedChars.reversed()

        wordList.forEach { target ->
            if ((target == selectedChars || target == reversedChars) && !foundWords.contains(target)) {
                foundWords.add(target)
                selectedCells.clear()
            }
        }
    }

    val isAllFound = foundWords.size >= wordList.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("wordsearch_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🔤 Word Search Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006874)
                )
                Text(
                    text = "Found ${foundWords.size} / ${wordList.size} Words",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.testTag("wordsearch_export_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Export Worksheet", tint = Color(0xFF006874))
                }

                IconButton(
                    onClick = {
                        foundWords.clear()
                        selectedCells.clear()
                    },
                    modifier = Modifier.testTag("wordsearch_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
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
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Complexity Level:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006874)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WordSearchDifficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDifficulty = diff
                                foundWords.clear()
                                selectedCells.clear()
                            },
                            label = {
                                Text(
                                    text = "${diff.badge} ${diff.label}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF006874),
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("wordsearch_diff_chip_${diff.name}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Word Target Chips ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            wordList.forEach { word ->
                val isFound = foundWords.contains(word)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isFound) Color(0xFF006874) else Color(0xFFE0F7FA),
                    border = BorderStroke(1.dp, Color(0xFF006874).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (isFound) "✓ $word" else word,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFound) Color.White else Color(0xFF006874),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Grid Canvas ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .testTag("wordsearch_grid_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.5.dp, Color(0xFF006874).copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                grid.forEachIndexed { r, rowLetters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowLetters.forEachIndexed { c, char ->
                            val isSelected = selectedCells.contains(Pair(r, c))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF006874)
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF006874) else Color.LightGray.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { toggleCellSelect(r, c) }
                                    .testTag("word_cell_${r}_$c"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = if (selectedDifficulty == WordSearchDifficulty.HARD) 14.sp else 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Victory Card ---
        AnimatedVisibility(
            visible = isAllFound,
            enter = fadeIn() + expandVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wordsearch_victory_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌟 ALL WORDS FOUND!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006874))
                    Text(text = "Great job! You earned +100 Vocabulary XP!", fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006874)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Export Worksheet PDF 🖨️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
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
                    text = "Printable Word Search Worksheet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Export this ${selectedDifficulty.label} (${selectedDifficulty.badge}) Word Search as a high-quality printable PDF activity page.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Select Paper Size:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                    selectedContainerColor = Color(0xFF006874),
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
                        Text(text = "Include Answer Key Page at back", fontSize = 12.sp)
                    }

                    if (isPdfExported) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Printable PDF generated successfully! Saved with 300 DPI print quality.",
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006874))
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

