package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.QuizDatabase
import com.example.data.entity.*
import com.example.data.repository.QuizRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TimerMode(val displayName: String, val durationSeconds: Int, val description: String) {
    PRACTICE("Untimed Practice", 0, "Learn with no time limit"),
    SAT("SAT Prep Mode", 60, "60s per question limit"),
    NTS("NTS Prep Mode", 30, "30s per question limit")
}

enum class GameMode(val title: String, val badge: String, val desc: String) {
    CLASSIC("Standard Trivia", "🎯 Classic", "10 Curated questions in your favorite category"),
    SPEED_BLITZ("Speed Blitz 60s", "⚡ Blitz", "Answer as many rapid questions as possible in 60 seconds"),
    SURVIVAL("Sudden Death", "💀 Survival", "3 Lives! One mistake loses a heart. How far can you go?"),
    DAILY_CHALLENGE("Daily Master Challenge", "🌟 2x Rewards", "Unique 5-question daily puzzle with double XP and bonus gems"),
    OFFLINE_PRACTICE("Offline Study & Review", "🧘 Practice", "Learn without timers or pressure. Review instant explanations"),
    SAT_PREP("SAT Prep Simulator", "🎓 SAT", "Timed standardized questions with complete reasoning"),
    NTS_PREP("NTS Prep Simulator", "📝 NTS", "Fast-paced aptitude, quantitative and analytical reasoning")
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    init {
        val database = QuizDatabase.getDatabase(application)
        repository = QuizRepository(database.quizDao())

        // Ensure database has initial seed data
        viewModelScope.launch {
            repository.ensureDatabasePopulated()
        }
    }

    // --- State Expositions ---
    val userProgress: StateFlow<UserProgress?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val quizAttempts: StateFlow<List<QuizAttempt>> = repository.quizAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Leaderboard division selector
    private val _leaderboardDivision = MutableStateFlow("KIDS")
    val leaderboardDivision: StateFlow<String> = _leaderboardDivision.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    // Leaderboard arena filter (Global vs Friends)
    private val _leaderboardArenaMode = MutableStateFlow("GLOBAL") // GLOBAL or FRIENDS
    val leaderboardArenaMode: StateFlow<String> = _leaderboardArenaMode.asStateFlow()

    fun setLeaderboardArenaMode(mode: String) {
        _leaderboardArenaMode.value = mode
    }

    init {
        viewModelScope.launch {
            _leaderboardDivision.collectLatest { division ->
                repository.getLeaderboard(division).collect { list ->
                    _leaderboard.value = list
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            userProgress.collect { user ->
                if (user != null) {
                    _leaderboardDivision.value = user.ageDivision
                }
            }
        }
    }

    fun setLeaderboardDivision(division: String) {
        _leaderboardDivision.value = division
    }

    // --- Practice/Prep Timer Selection State ---
    private val _selectedTimerMode = MutableStateFlow(TimerMode.PRACTICE)
    val selectedTimerMode: StateFlow<TimerMode> = _selectedTimerMode.asStateFlow()

    fun setTimerMode(mode: TimerMode) {
        _selectedTimerMode.value = mode
    }

    // --- Active Quiz State ---
    private val _activeQuiz = MutableStateFlow<ActiveQuizState?>(null)
    val activeQuiz: StateFlow<ActiveQuizState?> = _activeQuiz.asStateFlow()

    data class ActiveQuizState(
        val category: String,
        val ageDivision: String,
        val gameMode: GameMode = GameMode.CLASSIC,
        val questions: List<Question>,
        val currentIndex: Int = 0,
        val selectedOption: String? = null,
        val correctCount: Int = 0,
        val comboStreak: Int = 0,
        val livesRemaining: Int = 3,
        val isCompleted: Boolean = false,
        val isLoading: Boolean = false,
        val timerMode: TimerMode = TimerMode.PRACTICE,
        val wrongQuestions: List<Question> = emptyList(),
        val xpEarned: Int = 0
    ) {
        val currentQuestion: Question?
            get() = questions.getOrNull(currentIndex)
        val isGameOver: Boolean
            get() = gameMode == GameMode.SURVIVAL && livesRemaining <= 0
    }

    // Available Avatar Cosmetics catalog
    val availableAvatars = listOf(
        AvatarCosmetic("avatar_scholar", "Novice Scholar", "🎓", "Classic academic avatar for curious minds", 0, 0),
        AvatarCosmetic("avatar_owl", "Wise Owl", "🦉", "Perceptive and sharp intellect", 100, 0),
        AvatarCosmetic("avatar_astronaut", "Cosmic Voyager", "🚀", "Exploring the farthest frontiers of science", 250, 0),
        AvatarCosmetic("avatar_ninja", "Shadow Master", "🥷", "Swift reflexes and deadly trivia accuracy", 300, 1),
        AvatarCosmetic("avatar_wizard", "Archmage", "🧙", "Master of all magical knowledge and history", 400, 2),
        AvatarCosmetic("avatar_crown", "Golden Monarch", "👑", "For true royalty and trivia champions", 500, 2),
        AvatarCosmetic("avatar_cyborg", "Quantum AI", "🤖", "Futuristic high-frequency neural processing", 600, 3, isPremiumOnly = true)
    )

    // Dynamic Achievements Generation
    fun getAchievements(user: UserProgress?, attempts: List<QuizAttempt>): List<AchievementItem> {
        val totalXp = user?.totalXp ?: 0
        val streak = user?.streakCount ?: 0
        val claimedIds = user?.claimedAchievements?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
        val totalQuizzes = attempts.size
        val perfectRuns = attempts.count { it.score == it.totalQuestions && it.totalQuestions > 0 }
        val blitzRuns = attempts.count { it.gameMode == GameMode.SPEED_BLITZ.name }
        val survivalRuns = attempts.count { it.gameMode == GameMode.SURVIVAL.name }
        val uniqueCategories = attempts.map { it.category }.distinct().size

        val definitions = listOf(
            AchievementItem(
                id = "ach_first_quiz",
                title = "🎯 First Step",
                description = "Complete your very first quiz round",
                icon = "🎯",
                xpReward = 50,
                gemReward = 1,
                currentProgress = totalQuizzes.coerceAtMost(1),
                maxProgress = 1,
                isUnlocked = totalQuizzes >= 1,
                isClaimed = claimedIds.contains("ach_first_quiz")
            ),
            AchievementItem(
                id = "ach_streak_3",
                title = "🔥 Streak Pioneer",
                description = "Reach a 3-day active learning streak",
                icon = "🔥",
                xpReward = 100,
                gemReward = 2,
                currentProgress = streak.coerceAtMost(3),
                maxProgress = 3,
                isUnlocked = streak >= 3,
                isClaimed = claimedIds.contains("ach_streak_3")
            ),
            AchievementItem(
                id = "ach_streak_7",
                title = "⚡ Streak Master",
                description = "Maintain a 7-day daily quiz streak",
                icon = "⚡",
                xpReward = 250,
                gemReward = 5,
                currentProgress = streak.coerceAtMost(7),
                maxProgress = 7,
                isUnlocked = streak >= 7,
                isClaimed = claimedIds.contains("ach_streak_7")
            ),
            AchievementItem(
                id = "ach_perfection",
                title = "🌟 Flawless Scholar",
                description = "Score 100% accuracy on any quiz attempt",
                icon = "🌟",
                xpReward = 150,
                gemReward = 2,
                currentProgress = perfectRuns.coerceAtMost(1),
                maxProgress = 1,
                isUnlocked = perfectRuns >= 1,
                isClaimed = claimedIds.contains("ach_perfection")
            ),
            AchievementItem(
                id = "ach_polymath",
                title = "🧠 True Polymath",
                description = "Play quizzes across 4 different subject categories",
                icon = "🧠",
                xpReward = 200,
                gemReward = 3,
                currentProgress = uniqueCategories.coerceAtMost(4),
                maxProgress = 4,
                isUnlocked = uniqueCategories >= 4,
                isClaimed = claimedIds.contains("ach_polymath")
            ),
            AchievementItem(
                id = "ach_blitz_master",
                title = "⏱️ Speed Demon",
                description = "Complete at least 2 Speed Blitz timed challenges",
                icon = "⏱️",
                xpReward = 120,
                gemReward = 2,
                currentProgress = blitzRuns.coerceAtMost(2),
                maxProgress = 2,
                isUnlocked = blitzRuns >= 2,
                isClaimed = claimedIds.contains("ach_blitz_master")
            ),
            AchievementItem(
                id = "ach_survivalist",
                title = "🛡️ Iron Mind",
                description = "Test your mettle in Sudden Death Survival Mode",
                icon = "🛡️",
                xpReward = 150,
                gemReward = 2,
                currentProgress = survivalRuns.coerceAtMost(1),
                maxProgress = 1,
                isUnlocked = survivalRuns >= 1,
                isClaimed = claimedIds.contains("ach_survivalist")
            ),
            AchievementItem(
                id = "ach_grandmaster",
                title = "👑 Trivia Grandmaster",
                description = "Accumulate 1,000 total lifetime XP",
                icon = "👑",
                xpReward = 500,
                gemReward = 10,
                currentProgress = totalXp.coerceAtMost(1000),
                maxProgress = 1000,
                isUnlocked = totalXp >= 1000,
                isClaimed = claimedIds.contains("ach_grandmaster")
            )
        )

        return definitions
    }

    // --- User Actions ---
    fun updateProfile(username: String, ageDivision: String) {
        viewModelScope.launch {
            repository.updateUsernameAndAge(username, ageDivision)
            _leaderboardDivision.value = ageDivision
        }
    }

    fun equipAvatar(avatarId: String) {
        viewModelScope.launch {
            repository.setAvatar(avatarId)
        }
    }

    fun purchaseAvatar(avatarId: String, coinCost: Int, gemCost: Int) {
        viewModelScope.launch {
            repository.unlockAvatar(avatarId, coinCost, gemCost)
        }
    }

    fun buyStreakFreeze() {
        viewModelScope.launch {
            repository.buyStreakFreeze()
        }
    }

    fun activateVipPass() {
        viewModelScope.launch {
            repository.activateVipPass()
        }
    }

    fun claimAchievement(achievement: AchievementItem) {
        viewModelScope.launch {
            repository.claimAchievement(achievement.id, achievement.xpReward, achievement.gemReward)
        }
    }

    fun startQuiz(
        category: String,
        ageDivision: String,
        gameMode: GameMode = GameMode.CLASSIC,
        questionDivision: String = ageDivision
    ) {
        val currentTimer = when (gameMode) {
            GameMode.SAT_PREP -> TimerMode.SAT
            GameMode.NTS_PREP -> TimerMode.NTS
            GameMode.OFFLINE_PRACTICE -> TimerMode.PRACTICE
            else -> _selectedTimerMode.value
        }

        _activeQuiz.value = ActiveQuizState(
            category = category,
            ageDivision = ageDivision,
            gameMode = gameMode,
            questions = emptyList(),
            timerMode = currentTimer,
            isLoading = true
        )

        viewModelScope.launch {
            val questions = repository.getQuestionsForQuiz(category, questionDivision)
            _activeQuiz.value = ActiveQuizState(
                category = category,
                ageDivision = ageDivision,
                gameMode = gameMode,
                questions = questions,
                timerMode = currentTimer,
                isLoading = false
            )
        }
    }

    fun answerQuestion(option: String) {
        val state = _activeQuiz.value ?: return
        if (state.selectedOption != null) return // Already answered

        val currentQuestion = state.currentQuestion ?: return
        val isCorrect = option == currentQuestion.correctOption

        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount
        val newCombo = if (isCorrect) state.comboStreak + 1 else 0
        val newLives = if (!isCorrect && state.gameMode == GameMode.SURVIVAL) state.livesRemaining - 1 else state.livesRemaining

        val updatedWrongList = if (!isCorrect) {
            state.wrongQuestions + currentQuestion
        } else {
            state.wrongQuestions
        }

        _activeQuiz.value = state.copy(
            selectedOption = option,
            correctCount = newCorrectCount,
            comboStreak = newCombo,
            livesRemaining = newLives,
            wrongQuestions = updatedWrongList
        )
    }

    fun nextQuestion() {
        val state = _activeQuiz.value ?: return

        // If survival mode and lives reached 0, or end of questions reached
        val isEndReached = state.currentIndex >= state.questions.size - 1 || (state.gameMode == GameMode.SURVIVAL && state.livesRemaining <= 0)

        if (!isEndReached) {
            _activeQuiz.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOption = null
            )
        } else {
            // Quiz completed!
            viewModelScope.launch {
                val wrongSummary = state.wrongQuestions.joinToString(";") { it.text }
                val earnedXp = repository.completeQuiz(
                    category = state.category,
                    ageDivision = state.ageDivision,
                    correctAnswers = state.correctCount,
                    totalQuestions = state.questions.size,
                    gameMode = state.gameMode.name,
                    wrongSummary = wrongSummary
                )
                _activeQuiz.value = state.copy(
                    isCompleted = true,
                    xpEarned = earnedXp
                )
            }
        }
    }

    fun exitQuiz() {
        _activeQuiz.value = null
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllData()
            _activeQuiz.value = null
        }
    }
}
