package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameMode
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.TimerMode
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MyApplicationTheme { MainAppContent(viewModel()) } }
    }
}

private data class MenuChoice(
    val id: String,
    val icon: String,
    val label: String,
    val detail: String = ""
)

private val interests = listOf(
    MenuChoice("COUNTRIES", "🌍", "World", "Countries & geography"),
    MenuChoice("SCIENCE", "🔬", "Science", "Discover how things work"),
    MenuChoice("HISTORY", "🏛️", "History", "People, places & the past"),
    MenuChoice("MATHS", "🔢", "Maths", "Numbers & problem solving"),
    MenuChoice("GK", "💡", "General knowledge", "A little of everything"),
    MenuChoice("BRAIN_TEASERS", "🧩", "Brain teasers", "Think outside the box")
)
private val ages = listOf(
    MenuChoice("KIDS", "🌱", "Ages 5–8"),
    MenuChoice("JUNIORS", "🌿", "Ages 9–12"),
    MenuChoice("SENIORS", "🌳", "Ages 13+")
)
private val levels = listOf(
    MenuChoice("EASY", "🌱", "Easy", "Start with the basics"),
    MenuChoice("MEDIUM", "🔥", "Medium", "A little more challenge"),
    MenuChoice("HARD", "⚡", "Hard", "Put your knowledge to work")
)

/** A fixed-height page with persistent navigation; choices paginate instead of scrolling. */
@Composable
private fun ChoicePage(
    title: String,
    subtitle: String,
    choices: List<MenuChoice>,
    onChoice: (String) -> Unit,
    selectedIds: Set<String> = emptySet(),
    onBack: (() -> Unit)? = null,
    nextLabel: String? = null,
    onNext: () -> Unit = {},
    onSkip: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var page by rememberSaveable(title) { mutableIntStateOf(0) }
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(Modifier.safeDrawingPadding().padding(horizontal = 20.dp, vertical = 8.dp)) {
            val fontScale = LocalDensity.current.fontScale
            val compact = maxHeight < 500.dp || fontScale > 1.3f
            val pageSize = if (compact) 2 else 4
            val pages = ((choices.size + pageSize - 1) / pageSize).coerceAtLeast(1)
            val visiblePage = page.coerceIn(0, pages - 1)
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (onBack != null) TextButton(onClick = onBack) { Text("Back") }
                    else Text("QUIZ QUEST", modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    if (onSkip != null) TextButton(onClick = onSkip, modifier = Modifier.testTag("skip_setup")) { Text("Skip") }
                }
                Text(title, style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold)
                if (!compact && subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
                    choices.drop(visiblePage * pageSize).take(pageSize).forEach { choice ->
                        val chosen = choice.id in selectedIds
                        OutlinedCard(
                            onClick = { onChoice(choice.id) },
                            modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(min = 56.dp)
                                .testTag("choice_${choice.id}").semantics { selected = chosen },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(if (chosen) 2.dp else 1.dp,
                                if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.outlinedCardColors(containerColor = if (chosen)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(choice.icon, fontSize = if (compact) 22.sp else 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(choice.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    if (!compact && choice.detail.isNotEmpty()) Text(choice.detail,
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (chosen) Text("✓", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                if (pages > 1) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { page = visiblePage - 1 }, enabled = visiblePage > 0) { Text("Previous") }
                    Text("${visiblePage + 1} / $pages", style = MaterialTheme.typography.labelMedium)
                    TextButton(onClick = { page = visiblePage + 1 }, enabled = visiblePage < pages - 1,
                        modifier = Modifier.testTag("more_choices")) { Text("More") }
                }
                if (nextLabel != null) Button(onClick = onNext,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("continue_setup")) { Text(nextLabel) }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: QuizViewModel) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("quiz_layout_preferences", 0) }
    val user by viewModel.userProgress.collectAsStateWithLifecycle()
    val attempts by viewModel.quizAttempts.collectAsStateWithLifecycle()
    val quiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val rankDivision by viewModel.leaderboardDivision.collectAsStateWithLifecycle()
    var setupComplete by rememberSaveable { mutableStateOf(prefs.getBoolean("setup_complete", false)) }
    var screen by rememberSaveable { mutableStateOf("SPLASH") }
    var selectedInterests by rememberSaveable { mutableStateOf(prefs.getString("interests", "") ?: "") }
    var age by rememberSaveable { mutableStateOf(prefs.getString("age", "") ?: "") }
    var difficulty by rememberSaveable { mutableStateOf(prefs.getString("difficulty", "EASY") ?: "EASY") }
    var editingSetup by rememberSaveable { mutableStateOf(false) }
    val timerMode by viewModel.selectedTimerMode.collectAsStateWithLifecycle()
    var rankFriends by rememberSaveable { mutableStateOf(false) }
    var nickname by rememberSaveable { mutableStateOf("") }
    var resetDialog by remember { mutableStateOf(false) }
    val preferred = selectedInterests.split(',').filter { it.isNotBlank() }.toSet()

    fun saveChoices() {
        prefs.edit().putBoolean("setup_complete", true).putString("interests", selectedInterests)
            .putString("age", age).putString("difficulty", difficulty).apply()
        if (age.isNotEmpty() && user != null) viewModel.updateProfile(user!!.username, age)
        setupComplete = true
        editingSetup = false
        screen = "HOME"
    }
    fun launchQuiz(category: String, mode: GameMode = GameMode.CLASSIC) {
        val contentDivision = when (difficulty) { "HARD" -> "SENIORS"; "MEDIUM" -> "JUNIORS"; else -> "KIDS" }
        viewModel.startQuiz(category, user?.ageDivision ?: "KIDS", mode, questionDivision = contentDivision)
    }
    fun back() {
        screen = when (screen) {
            "AGE" -> "INTERESTS"
            "DIFFICULTY" -> "AGE"
            "INTERESTS" -> if (editingSetup) "PROFILE" else "WELCOME"
            "WELCOME" -> "HOME"
            "NAME", "HISTORY", "STATS", "RESET" -> "PROFILE"
            "RANKS" -> "RANK_FILTER"
            "MAZE", "WORD_SEARCH", "SUDOKU" -> "PUZZLES"
            else -> "HOME"
        }
    }
    BackHandler(enabled = quiz == null && screen != "HOME" && screen != "SPLASH") { back() }
    LaunchedEffect(screen, user) {
        if (screen == "SPLASH") {
            delay(700)
            if (user != null) screen = if (setupComplete) "HOME" else "INTERESTS"
        }
    }

    val active = quiz
    if (active != null) {
        if (active.isCompleted) {
            ChoicePage("Round complete!", "${active.correctCount} / ${active.questions.size} correct · +${active.xpEarned} XP",
                listOf(MenuChoice("HOME", "🏠", "Back to home"), MenuChoice("RANKS", "🏆", "View rankings")),
                onChoice = { viewModel.exitQuiz(); screen = if (it == "RANKS") "RANK_FILTER" else "HOME" })
        } else {
            QuizScreen(quizState = active, onAnswerSelected = viewModel::answerQuestion,
                onNextClicked = viewModel::nextQuestion, onExitQuiz = viewModel::exitQuiz)
        }
        return
    }
    when (screen) {
        "SPLASH" -> Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.safeDrawingPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text("🧠", fontSize = 64.sp)
                Text("Quiz Quest", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                Text("A little curiosity. A new discovery.", modifier = Modifier.padding(vertical = 16.dp))
                CircularProgressIndicator(Modifier.size(28.dp))
            }
        }
        "WELCOME" -> ChoicePage("Make it your quest", "Personalize your topics and challenge level. Everything is optional.",
            listOf(MenuChoice("SETUP", "✨", "Personalize my quizzes", "Choose interests, age and difficulty"),
                MenuChoice("QUICK", "🎮", "Start exploring", "All topics · Easy difficulty")),
            onChoice = { if (it == "SETUP") screen = "INTERESTS" else saveChoices() })
        "INTERESTS" -> ChoicePage("What interests you?", "Choose any topics. You can always explore everything later.",
            interests, selectedIds = preferred, onChoice = { id ->
                selectedInterests = (if (id in preferred) preferred - id else preferred + id).joinToString(",")
            }, onBack = if (editingSetup) ::back else null, nextLabel = "Continue", onNext = { screen = "AGE" },
            onSkip = { selectedInterests = ""; screen = "AGE" })
        "AGE" -> ChoicePage("Your age group", "Optional. Your age group and question difficulty are separate choices.",
            ages, selectedIds = setOf(age), onChoice = { age = if (age == it) "" else it }, onBack = ::back,
            nextLabel = "Choose difficulty", onNext = { screen = "DIFFICULTY" },
            onSkip = { age = ""; screen = "DIFFICULTY" })
        "DIFFICULTY" -> ChoicePage("Choose your challenge", "Easy is the default. You can change this any time in your profile.",
            levels, selectedIds = setOf(difficulty), onChoice = { difficulty = it }, onBack = ::back,
            nextLabel = "Let's play", onNext = ::saveChoices, onSkip = { difficulty = "EASY"; saveChoices() })
        "HOME" -> ChoicePage("Ready to discover?", "${user?.username ?: "Explorer"} · ${difficulty.lowercase().replaceFirstChar { it.uppercase() }} difficulty",
            listOf(MenuChoice("TOPICS", "🎯", "Play a quiz", "Pick a topic you love"),
                MenuChoice("CHALLENGES", "⚡", "Challenges", "Daily, speed & survival"),
                MenuChoice("PUZZLES", "🧩", "Puzzle room", "Maze, word search & Sudoku"),
                MenuChoice("STUDY", "📚", "Study & exam prep", "Practice, SAT & NTS"),
                MenuChoice("RANK_FILTER", "🏆", "Rankings", "Your local practice league"),
                MenuChoice("PROFILE", "👤", "Your profile", "Preferences, progress & history")), onChoice = { screen = it })
        "TOPICS", "PRACTICE_TOPICS" -> ChoicePage("Pick a topic", "Your interests appear first. More topics are one tap away.",
            interests.sortedByDescending { it.id in preferred } + MenuChoice("ALL", "🎲", "Mix all topics"),
            onChoice = { launchQuiz(it, if (screen == "PRACTICE_TOPICS") GameMode.OFFLINE_PRACTICE else GameMode.CLASSIC) },
            onBack = ::back)
        "CHALLENGES" -> ChoicePage("Choose a challenge", "A different way to test your knowledge.", listOf(
            MenuChoice("DAILY_CHALLENGE", "🌟", "Daily challenge", "5 questions · Bonus rewards"),
            MenuChoice("SPEED_BLITZ", "⚡", "Speed Blitz", "Beat the clock"),
            MenuChoice("SURVIVAL", "❤️", "Survival", "Three lives to keep going")),
            onChoice = { launchQuiz(it, GameMode.valueOf(it)) }, onBack = ::back)
        "PUZZLES" -> ChoicePage("Puzzle room", "Choose your next brain workout.", listOf(
            MenuChoice("MAZE", "🌀", "Find the path"), MenuChoice("WORD_SEARCH", "🔤", "Word search"),
            MenuChoice("SUDOKU", "🔢", "Mini Sudoku")), onChoice = { screen = it }, onBack = ::back)
        "MAZE" -> InteractiveMazeScreen(onBack = { screen = "PUZZLES" }, modifier = Modifier.safeDrawingPadding())
        "WORD_SEARCH" -> WordSearchScreen(onBack = { screen = "PUZZLES" }, modifier = Modifier.safeDrawingPadding())
        "SUDOKU" -> SudokuScreen(onBack = { screen = "PUZZLES" }, modifier = Modifier.safeDrawingPadding())
        "STUDY" -> ChoicePage("Study your way", "Take your time or practice under pressure.", listOf(
            MenuChoice("PRACTICE_TOPICS", "📖", "Untimed practice"), MenuChoice("SAT_PREP", "🎓", "SAT preparation"),
            MenuChoice("NTS_PREP", "📝", "NTS preparation"), MenuChoice("TIMER", "⏱️", "Quiz timer settings")),
            onChoice = { if (it == "SAT_PREP" || it == "NTS_PREP") launchQuiz(it, GameMode.valueOf(it)) else screen = it }, onBack = ::back)
        "TIMER" -> ChoicePage("Quiz timing", "Applies to standard quizzes. Exam prep uses its own timer.",
            TimerMode.entries.map { MenuChoice(it.name, "⏱️", it.displayName, it.description) }, selectedIds = setOf(timerMode.name),
            onChoice = { viewModel.setTimerMode(TimerMode.valueOf(it)) }, onBack = { screen = "STUDY" },
            nextLabel = "Done", onNext = { screen = "STUDY" })
        "RANK_FILTER" -> ChoicePage("Choose a league", "Practice rankings use simulated competitors on this device.",
            ages, selectedIds = setOf(rankDivision), onChoice = { viewModel.setLeaderboardDivision(it); screen = "RANKS" }, onBack = ::back)
        "RANKS" -> ChoicePage(if (rankFriends) "Friends league" else "Practice league", "${rankDivision.lowercase()} · Simulated local rankings",
            listOf(MenuChoice("FILTER", "👥", if (rankFriends) "Show everyone" else "Show friends")) +
                leaderboard.filter { !rankFriends || it.isFriend || it.isMe }.mapIndexed { index, entry ->
                    MenuChoice("rank_${entry.id}", if (entry.isMe) "👤" else "🏅", "${index + 1}. ${entry.username}", "${entry.score} XP")
                }, onChoice = { if (it == "FILTER") rankFriends = !rankFriends }, onBack = ::back)
        "PROFILE" -> ChoicePage("Your space", "${user?.username ?: "Explorer"} · ${user?.totalXp ?: 0} XP", listOf(
            MenuChoice("NAME", "✏️", "Edit nickname"), MenuChoice("INTERESTS", "✨", "Interests, age & difficulty"),
            MenuChoice("STATS", "📊", "My progress"), MenuChoice("HISTORY", "🗂️", "Quiz history"),
            MenuChoice("RESET", "↺", "Reset progress")), onChoice = {
                if (it == "INTERESTS") editingSetup = true
                if (it == "NAME") nickname = user?.username ?: "Explorer"
                screen = it
            }, onBack = ::back)
        "NAME" -> Surface(Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().imePadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = ::back) { Text("Back") }
                Text("Your nickname", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(value = nickname, onValueChange = { nickname = it.take(24) }, label = { Text("Nickname") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                Button(onClick = { viewModel.updateProfile(nickname.trim(), user?.ageDivision ?: "KIDS"); screen = "PROFILE" },
                    enabled = nickname.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("Save") }
            }
        }
        "STATS" -> ChoicePage("Your progress", "Keep your curiosity growing.", listOf(
            MenuChoice("XP", "⭐", "${user?.totalXp ?: 0} XP", "Level ${user?.level ?: 1}"),
            MenuChoice("STREAK", "🔥", "${user?.streakCount ?: 0}-day streak", "Best: ${user?.longestStreak ?: 0} days"),
            MenuChoice("WALLET", "💎", "${user?.coins ?: 0} coins · ${user?.gems ?: 0} gems"),
            MenuChoice("ATTEMPTS", "🎯", "${attempts.size} quizzes completed")), onChoice = {}, onBack = ::back)
        "HISTORY" -> ChoicePage("Your quiz history", if (attempts.isEmpty()) "Your first discovery is waiting. Play a quiz to begin." else "Newest rounds first.",
            attempts.map { MenuChoice("attempt_${it.id}", "📝", it.category.replace('_', ' '), "${it.score} / ${it.totalQuestions} correct") },
            onChoice = {}, onBack = ::back)
        "RESET" -> ChoicePage("Reset your progress?", "This erases quiz history, rewards and your profile on this device.",
            listOf(MenuChoice("CANCEL", "🏠", "Keep my progress"), MenuChoice("CONFIRM", "↺", "Reset everything")),
            onChoice = { if (it == "CONFIRM") resetDialog = true else screen = "PROFILE" }, onBack = ::back)
    }
    if (resetDialog) AlertDialog(onDismissRequest = { resetDialog = false }, title = { Text("Erase local progress?") },
        text = { Text("This cannot be undone.") }, dismissButton = { TextButton(onClick = { resetDialog = false }) { Text("Cancel") } },
        confirmButton = { TextButton(onClick = {
            resetDialog = false; viewModel.resetAllProgress(); prefs.edit().clear().apply()
            setupComplete = false; selectedInterests = ""; age = ""; difficulty = "EASY"; screen = "INTERESTS"
        }) { Text("Reset") } })
}

// Kept as a preview/test entry point for the existing screenshot test.
@Composable
fun OnboardingWelcomeScreen(onGetStarted: (String, String) -> Unit, modifier: Modifier = Modifier) {
    ChoicePage("Make it your quest", "Choose interests and difficulty, or start exploring now.",
        listOf(MenuChoice("START", "🎮", "Start exploring")), onChoice = { onGetStarted("Explorer", "KIDS") }, modifier = modifier)
}
