package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.TimerMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: QuizViewModel = viewModel()
                MainAppContent(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: QuizViewModel) {
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val quizAttempts by viewModel.quizAttempts.collectAsStateWithLifecycle()
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val leaderboardDivision by viewModel.leaderboardDivision.collectAsStateWithLifecycle()
    val leaderboardArenaMode by viewModel.leaderboardArenaMode.collectAsStateWithLifecycle()
    val selectedTimerMode by viewModel.selectedTimerMode.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    var activeSubScreen by remember { mutableStateOf<String?>(null) }
    
    // Onboarding State (True if user is still "Explorer", meaning they haven't configured their name yet)
    val isFirstLaunch = userProgress != null && userProgress?.username == "Explorer"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            // Sub-screen 1: Interactive Canvas Maze
            activeSubScreen == "MAZE" -> {
                InteractiveMazeScreen(
                    onBack = { activeSubScreen = null },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // Sub-screen 2: Interactive Word Search
            activeSubScreen == "WORD_SEARCH" -> {
                WordSearchScreen(
                    onBack = { activeSubScreen = null },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // Sub-screen 3: Interactive 4x4 Sudoku
            activeSubScreen == "SUDOKU" -> {
                SudokuScreen(
                    onBack = { activeSubScreen = null },
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // 1. Active Quiz Round
            activeQuiz != null -> {
                val state = activeQuiz!!
                if (!state.isCompleted) {
                    QuizScreen(
                        quizState = state,
                        onAnswerSelected = { option -> viewModel.answerQuestion(option) },
                        onNextClicked = { viewModel.nextQuestion() },
                        onExitQuiz = { viewModel.exitQuiz() }
                    )
                } else {
                    ResultScreen(
                        score = state.correctCount,
                        totalQuestions = state.questions.size,
                        category = state.category,
                        ageDivision = state.ageDivision,
                        streak = userProgress?.streakCount ?: 0,
                        onBackHome = { viewModel.exitQuiz() },
                        onViewLeaderboard = {
                            viewModel.exitQuiz()
                            currentTab = 1 // Go to Leaderboard tab
                        }
                    )
                }
            }

            // 2. Onboarding Setup (First Launch Overlay)
            isFirstLaunch -> {
                OnboardingWelcomeScreen(
                    onGetStarted = { nickname, division ->
                        viewModel.updateProfile(nickname, division)
                    }
                )
            }

            // 3. Main Dashboard Navigation Hub
            else -> {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Games & Quizzes") },
                                label = { Text("Games") },
                                modifier = Modifier.testTag("nav_tab_quizzes")
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") },
                                label = { Text("Ranks") },
                                modifier = Modifier.testTag("nav_tab_leaderboard")
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile") },
                                modifier = Modifier.testTag("nav_tab_profile")
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            0 -> DashboardScreen(
                                userProgress = userProgress,
                                selectedTimerMode = selectedTimerMode,
                                onTimerModeSelected = { mode -> viewModel.setTimerMode(mode) },
                                onStartQuiz = { category ->
                                    val division = userProgress?.ageDivision ?: "KIDS"
                                    viewModel.startQuiz(category, division)
                                },
                                onStartGameMode = { mode, category ->
                                    val division = userProgress?.ageDivision ?: "KIDS"
                                    viewModel.startQuiz(category, division, mode)
                                },
                                onUpdateAgeDivision = { newDivision ->
                                    val currentName = userProgress?.username ?: "Explorer"
                                    viewModel.updateProfile(currentName, newDivision)
                                },
                                onOpenMaze = { activeSubScreen = "MAZE" },
                                onOpenWordSearch = { activeSubScreen = "WORD_SEARCH" },
                                onOpenSudoku = { activeSubScreen = "SUDOKU" },
                                modifier = Modifier.statusBarsPadding()
                            )
                            1 -> LeaderboardScreen(
                                currentDivision = leaderboardDivision,
                                leaderboardEntries = leaderboard,
                                arenaMode = leaderboardArenaMode,
                                onSelectDivision = { division -> viewModel.setLeaderboardDivision(division) },
                                onSelectArenaMode = { mode -> viewModel.setLeaderboardArenaMode(mode) },
                                modifier = Modifier.statusBarsPadding()
                            )
                            2 -> ProfileScreen(
                                userProgress = userProgress,
                                quizAttempts = quizAttempts,
                                onUpdateProfile = { name, division -> viewModel.updateProfile(name, division) },
                                onResetAll = { viewModel.resetAllProgress() },
                                modifier = Modifier.statusBarsPadding()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingWelcomeScreen(
    onGetStarted: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf("KIDS") }
    var errorText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🧠",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Welcome to Quiz Quest!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Select your age difficulty division and choose a nickname to begin your trivia adventure!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nickname Input
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        if (it.isNotBlank()) errorText = ""
                    },
                    label = { Text("Enter Nickname") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input"),
                    singleLine = true,
                    supportingText = {
                        if (errorText.isNotBlank()) {
                            Text(text = errorText, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Age Divisions Selection
                Text(
                    text = "Select Age Difficulty:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

                val divisionsList = listOf(
                    "KIDS" to "👶 Kids (Ages 5-8)",
                    "JUNIORS" to "🎒 Juniors (Ages 9-12)",
                    "SENIORS" to "🎓 Seniors (Ages 13+)"
                )

                divisionsList.forEach { (key, label) ->
                    val isSelected = selectedAge == key
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                            .testTag("onboarding_age_$key")
                            .clickable { selectedAge = key },
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedAge = key }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Get Started Button
                Button(
                    onClick = {
                        if (nameInput.trim().isBlank()) {
                            errorText = "Please enter a valid nickname!"
                        } else {
                            onGetStarted(nameInput.trim(), selectedAge)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_start_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
