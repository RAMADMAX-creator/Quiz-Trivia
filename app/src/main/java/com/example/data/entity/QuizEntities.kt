package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1, // Single local user
    val username: String = "Explorer",
    val ageDivision: String = "KIDS", // KIDS, JUNIORS, SENIORS
    val streakCount: Int = 1,
    val longestStreak: Int = 1,
    val lastActiveTimestamp: Long = 0L,
    val totalXp: Int = 120,
    val coins: Int = 150,
    val gems: Int = 10,
    val selectedAvatar: String = "avatar_scholar", // avatar_scholar, avatar_astronaut, avatar_owl, avatar_crown, avatar_ninja, avatar_wizard, avatar_cyborg
    val unlockedAvatars: String = "avatar_scholar", // comma separated
    val streakFreezes: Int = 1,
    val isVip: Boolean = false,
    val isAdFree: Boolean = false,
    val lastDailyChallengeDate: String = "",
    val weeklyCompletedQuizzes: Int = 2,
    val weeklyGoalTarget: Int = 5,
    val claimedAchievements: String = "" // comma separated IDs
) {
    val level: Int get() = 1 + (totalXp / 200)
    val xpInCurrentLevel: Int get() = totalXp % 200
    val progressToNextLevel: Float get() = xpInCurrentLevel.toFloat() / 200f
    val levelTitle: String get() = when(level) {
        1 -> "Novice Explorer"
        2 -> "Trivia Apprentice"
        3 -> "Knowledge Seeker"
        4 -> "Quiz Adept"
        5 -> "Trivia Scholar"
        6 -> "Brain Maestro"
        7 -> "Mastermind"
        8 -> "Champion of Trivia"
        9 -> "Mythic Polymath"
        else -> "Grand Quiz Master"
    }
}

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // COUNTRIES, SCIENCE, HISTORY, MATHS, GK, SAT_PREP, NTS_PREP, BRAIN_TEASERS
    val ageDivision: String, // KIDS, JUNIORS, SENIORS
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // A, B, C, or D
    val explanation: String
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val score: Int,
    val ageDivision: String, // KIDS, JUNIORS, SENIORS
    val isMe: Boolean = false,
    val avatarSeed: Int = 1,
    val isFriend: Boolean = false,
    val league: String = "Gold" // Bronze, Silver, Gold, Platinum, Diamond, Master
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val ageDivision: String,
    val score: Int,
    val totalQuestions: Int,
    val gameMode: String = "CLASSIC", // CLASSIC, SPEED_BLITZ, SURVIVAL, DAILY_CHALLENGE, OFFLINE_PRACTICE
    val timestamp: Long = System.currentTimeMillis(),
    val wrongQuestionsSummary: String = "" // JSON or text summary for mistake review deck
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val gemReward: Int,
    val currentProgress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean,
    val isClaimed: Boolean
)

data class AvatarCosmetic(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val coinCost: Int,
    val gemCost: Int,
    val isPremiumOnly: Boolean = false
)

