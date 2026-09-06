package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    // --- User Progress ---
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)

    @Update
    suspend fun updateUserProgress(progress: UserProgress)


    // --- Questions ---
    @Query("SELECT * FROM questions WHERE category = :category AND ageDivision = :ageDivision ORDER BY RANDOM() LIMIT :limit")
    suspend fun getQuestionsForQuiz(category: String, ageDivision: String, limit: Int = 10): List<Question>

    @Query("SELECT * FROM questions WHERE ageDivision = :ageDivision ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsForDivision(ageDivision: String, limit: Int = 10): List<Question>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int = 10): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)


    // --- Leaderboard ---
    @Query("SELECT * FROM leaderboard WHERE ageDivision = :ageDivision ORDER BY score DESC")
    fun getLeaderboardFlow(ageDivision: String): Flow<List<LeaderboardEntry>>

    @Query("SELECT * FROM leaderboard WHERE ageDivision = :ageDivision ORDER BY score DESC")
    suspend fun getLeaderboard(ageDivision: String): List<LeaderboardEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntry>)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()

    @Query("DELETE FROM leaderboard WHERE isMe = 1 AND ageDivision = :ageDivision")
    suspend fun deleteMyLeaderboardEntry(ageDivision: String)


    // --- Quiz Attempts ---
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getQuizAttemptsFlow(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttempt)

    @Query("DELETE FROM quiz_attempts")
    suspend fun clearHistory()
}

