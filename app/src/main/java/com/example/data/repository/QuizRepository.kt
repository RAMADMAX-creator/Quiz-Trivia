package com.example.data.repository

import com.example.data.LeaderboardSeeds
import com.example.data.QuestionBank
import com.example.data.dao.QuizDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class QuizRepository(private val quizDao: QuizDao) {

    val userProgress: Flow<UserProgress?> = quizDao.getUserProgressFlow()
    val quizAttempts: Flow<List<QuizAttempt>> = quizDao.getQuizAttemptsFlow()

    fun getLeaderboard(ageDivision: String): Flow<List<LeaderboardEntry>> {
        return quizDao.getLeaderboardFlow(ageDivision)
    }

    // Ensure questions and mock leaderboard players are populated
    suspend fun ensureDatabasePopulated() {
        val questionCount = quizDao.getQuestionCount()
        if (questionCount == 0) {
            quizDao.insertQuestions(QuestionBank.questions)
        }

        val existingLeaderboard = quizDao.getLeaderboard("SENIORS")
        if (existingLeaderboard.isEmpty()) {
            quizDao.insertLeaderboardEntries(LeaderboardSeeds.seedEntries)
        }

        val user = quizDao.getUserProgress()
        if (user == null) {
            quizDao.insertUserProgress(UserProgress())
        }
    }

    suspend fun getQuestionsForQuiz(category: String, ageDivision: String, limit: Int = 10): List<Question> {
        return when (category) {
            "DAILY_CHALLENGE" -> quizDao.getRandomQuestionsForDivision(ageDivision, 5)
            "SPEED_BLITZ" -> quizDao.getRandomQuestionsForDivision(ageDivision, 15)
            "SURVIVAL" -> quizDao.getRandomQuestionsForDivision(ageDivision, 20)
            "OFFLINE_PRACTICE" -> quizDao.getRandomQuestionsForDivision(ageDivision, 10)
            "ALL" -> quizDao.getRandomQuestionsForDivision(ageDivision, limit)
            else -> {
                val list = quizDao.getQuestionsForQuiz(category, ageDivision, limit)
                if (list.isEmpty()) quizDao.getRandomQuestionsForDivision(ageDivision, limit) else list
            }
        }
    }

    suspend fun updateUsernameAndAge(username: String, ageDivision: String) {
        val currentUser = quizDao.getUserProgress() ?: UserProgress()
        
        if (currentUser.ageDivision != ageDivision) {
            quizDao.deleteMyLeaderboardEntry(currentUser.ageDivision)
        }

        val updatedUser = currentUser.copy(
            username = username,
            ageDivision = ageDivision
        )
        quizDao.insertUserProgress(updatedUser)
        syncUserToLeaderboard(updatedUser)
    }

    suspend fun setAvatar(avatarId: String) {
        val currentUser = quizDao.getUserProgress() ?: return
        val updatedUser = currentUser.copy(selectedAvatar = avatarId)
        quizDao.insertUserProgress(updatedUser)
    }

    suspend fun unlockAvatar(avatarId: String, coinCost: Int, gemCost: Int): Boolean {
        val currentUser = quizDao.getUserProgress() ?: return false
        if (currentUser.coins < coinCost || currentUser.gems < gemCost) return false

        val currentUnlocked = currentUser.unlockedAvatars.split(",").map { it.trim() }.toMutableSet()
        currentUnlocked.add(avatarId)

        val updatedUser = currentUser.copy(
            coins = currentUser.coins - coinCost,
            gems = currentUser.gems - gemCost,
            unlockedAvatars = currentUnlocked.joinToString(","),
            selectedAvatar = avatarId
        )
        quizDao.insertUserProgress(updatedUser)
        return true
    }

    suspend fun buyStreakFreeze(): Boolean {
        val currentUser = quizDao.getUserProgress() ?: return false
        if (currentUser.coins < 100) return false

        val updatedUser = currentUser.copy(
            coins = currentUser.coins - 100,
            streakFreezes = currentUser.streakFreezes + 1
        )
        quizDao.insertUserProgress(updatedUser)
        return true
    }

    suspend fun activateVipPass() {
        val currentUser = quizDao.getUserProgress() ?: return
        val currentUnlocked = currentUser.unlockedAvatars.split(",").map { it.trim() }.toMutableSet()
        currentUnlocked.addAll(listOf("avatar_crown", "avatar_ninja", "avatar_cyborg", "avatar_wizard"))

        val updatedUser = currentUser.copy(
            isVip = true,
            isAdFree = true,
            gems = currentUser.gems + 50,
            coins = currentUser.coins + 1000,
            unlockedAvatars = currentUnlocked.joinToString(",")
        )
        quizDao.insertUserProgress(updatedUser)
    }

    suspend fun claimAchievement(achievementId: String, xpReward: Int, gemReward: Int) {
        val currentUser = quizDao.getUserProgress() ?: return
        val claimedSet = currentUser.claimedAchievements.split(",").map { it.trim() }.toMutableSet()
        if (claimedSet.contains(achievementId)) return

        claimedSet.add(achievementId)
        val updatedUser = currentUser.copy(
            totalXp = currentUser.totalXp + xpReward,
            gems = currentUser.gems + gemReward,
            claimedAchievements = claimedSet.joinToString(",")
        )
        quizDao.insertUserProgress(updatedUser)
        syncUserToLeaderboard(updatedUser)
    }

    // Main logic for completing a quiz round
    suspend fun completeQuiz(
        category: String,
        ageDivision: String,
        correctAnswers: Int,
        totalQuestions: Int,
        gameMode: String = "CLASSIC",
        wrongSummary: String = ""
    ): Int {
        val currentUser = quizDao.getUserProgress() ?: UserProgress()
        val isDailyChallenge = gameMode == "DAILY_CHALLENGE"

        // 1. Calculate XP & Coin & Gem Rewards
        val xpPerCorrect = if (isDailyChallenge || currentUser.isVip) 30 else 15
        val completionXp = if (isDailyChallenge) 50 else 25
        val xpGained = (correctAnswers * xpPerCorrect) + completionXp
        val newXp = currentUser.totalXp + xpGained

        // Coins earned: 5 per correct + 10 bonus for perfect score
        val isPerfect = correctAnswers == totalQuestions && totalQuestions > 0
        val coinsGained = (correctAnswers * 5) + (if (isPerfect) 25 else 5)
        val gemsGained = if (isDailyChallenge && isPerfect) 2 else if (isDailyChallenge) 1 else 0

        // 2. Process Streak
        val now = System.currentTimeMillis()
        val lastActive = currentUser.lastActiveTimestamp
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))

        var newStreak = currentUser.streakCount
        var freezesLeft = currentUser.streakFreezes

        if (lastActive == 0L) {
            newStreak = 1
        } else {
            if (isSameDay(lastActive, now)) {
                // Already played today, streak remains intact
            } else if (isConsecutiveDay(lastActive, now)) {
                // Played yesterday, increment streak!
                newStreak += 1
            } else {
                // Missed a day! Check for streak freeze shield
                if (freezesLeft > 0) {
                    freezesLeft -= 1
                    newStreak += 1 // Saved by freeze shield!
                } else {
                    newStreak = 1
                }
            }
        }
        val newLongestStreak = maxOf(currentUser.longestStreak, newStreak)

        val updatedWeekly = currentUser.weeklyCompletedQuizzes + 1

        val updatedUser = currentUser.copy(
            streakCount = newStreak,
            longestStreak = newLongestStreak,
            lastActiveTimestamp = now,
            totalXp = newXp,
            coins = currentUser.coins + coinsGained,
            gems = currentUser.gems + gemsGained,
            streakFreezes = freezesLeft,
            lastDailyChallengeDate = if (isDailyChallenge) todayStr else currentUser.lastDailyChallengeDate,
            weeklyCompletedQuizzes = updatedWeekly
        )
        
        // Save updated progress
        quizDao.insertUserProgress(updatedUser)

        // 3. Record quiz attempt
        quizDao.insertQuizAttempt(
            QuizAttempt(
                category = category,
                ageDivision = ageDivision,
                score = correctAnswers,
                totalQuestions = totalQuestions,
                gameMode = gameMode,
                timestamp = now,
                wrongQuestionsSummary = wrongSummary
            )
        )

        // 4. Update the User's Entry on the Leaderboard
        syncUserToLeaderboard(updatedUser)

        // 5. Simulate competition
        simulateCompetitorProgress(ageDivision)

        return xpGained
    }

    private suspend fun syncUserToLeaderboard(user: UserProgress) {
        quizDao.deleteMyLeaderboardEntry(user.ageDivision)

        val myLeague = when {
            user.totalXp >= 1000 -> "Master"
            user.totalXp >= 600 -> "Diamond"
            user.totalXp >= 400 -> "Platinum"
            user.totalXp >= 250 -> "Gold"
            user.totalXp >= 100 -> "Silver"
            else -> "Bronze"
        }

        val myEntry = LeaderboardEntry(
            username = user.username + " (You)",
            score = user.totalXp,
            ageDivision = user.ageDivision,
            isMe = true,
            avatarSeed = 999,
            isFriend = false,
            league = myLeague
        )
        
        val currentLeaderboard = quizDao.getLeaderboard(user.ageDivision)
        val existingMe = currentLeaderboard.find { it.isMe }
        if (existingMe != null) {
            quizDao.insertLeaderboardEntries(listOf(myEntry.copy(id = existingMe.id)))
        } else {
            quizDao.insertLeaderboardEntries(listOf(myEntry))
        }
    }

    private suspend fun simulateCompetitorProgress(ageDivision: String) {
        val currentEntries = quizDao.getLeaderboard(ageDivision)
        if (currentEntries.isNotEmpty()) {
            val updatedEntries = currentEntries.map { entry ->
                if (!entry.isMe) {
                    val pointsIncrease = (5..25).random()
                    entry.copy(score = entry.score + pointsIncrease)
                } else {
                    entry
                }
            }
            quizDao.insertLeaderboardEntries(updatedEntries)
        }
    }

    suspend fun resetAllData() {
        quizDao.clearLeaderboard()
        quizDao.clearHistory()
        quizDao.insertUserProgress(UserProgress())
        quizDao.insertLeaderboardEntries(LeaderboardSeeds.seedEntries)
    }

    // --- Calendar Day Helpers ---
    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isConsecutiveDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        
        cal1.add(Calendar.DAY_OF_YEAR, 1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}

