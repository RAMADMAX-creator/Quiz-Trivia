package com.example.data

import com.example.data.entity.LeaderboardEntry

object LeaderboardSeeds {
    val seedEntries: List<LeaderboardEntry> = listOf(
        // --- KIDS ---
        LeaderboardEntry(username = "LeoTheLion", score = 350, ageDivision = "KIDS", avatarSeed = 1, isFriend = true, league = "Diamond"),
        LeaderboardEntry(username = "SparkyUnicorn", score = 310, ageDivision = "KIDS", avatarSeed = 2, isFriend = false, league = "Diamond"),
        LeaderboardEntry(username = "BunnyHop", score = 280, ageDivision = "KIDS", avatarSeed = 3, isFriend = true, league = "Platinum"),
        LeaderboardEntry(username = "PixelPuppy", score = 210, ageDivision = "KIDS", avatarSeed = 4, isFriend = false, league = "Gold"),
        LeaderboardEntry(username = "DinoDave", score = 150, ageDivision = "KIDS", avatarSeed = 5, isFriend = true, league = "Silver"),
        LeaderboardEntry(username = "StarKid", score = 90, ageDivision = "KIDS", avatarSeed = 6, isFriend = false, league = "Bronze"),

        // --- JUNIORS ---
        LeaderboardEntry(username = "BrainyBeaver", score = 620, ageDivision = "JUNIORS", avatarSeed = 10, isFriend = false, league = "Master"),
        LeaderboardEntry(username = "NovaVoyager", score = 580, ageDivision = "JUNIORS", avatarSeed = 11, isFriend = true, league = "Diamond"),
        LeaderboardEntry(username = "QuizWiz", score = 490, ageDivision = "JUNIORS", avatarSeed = 12, isFriend = true, league = "Platinum"),
        LeaderboardEntry(username = "CyberCheetah", score = 420, ageDivision = "JUNIORS", avatarSeed = 13, isFriend = false, league = "Gold"),
        LeaderboardEntry(username = "AtomAlchemist", score = 330, ageDivision = "JUNIORS", avatarSeed = 14, isFriend = true, league = "Silver"),
        LeaderboardEntry(username = "CodePanda", score = 250, ageDivision = "JUNIORS", avatarSeed = 15, isFriend = false, league = "Bronze"),

        // --- SENIORS ---
        LeaderboardEntry(username = "AetherEinstein", score = 980, ageDivision = "SENIORS", avatarSeed = 20, isFriend = false, league = "Master"),
        LeaderboardEntry(username = "ChronosScribe", score = 890, ageDivision = "SENIORS", avatarSeed = 21, isFriend = true, league = "Master"),
        LeaderboardEntry(username = "SophiaTheSage", score = 840, ageDivision = "SENIORS", avatarSeed = 22, isFriend = false, league = "Diamond"),
        LeaderboardEntry(username = "PythagorasPupil", score = 750, ageDivision = "SENIORS", avatarSeed = 23, isFriend = true, league = "Diamond"),
        LeaderboardEntry(username = "DarwinDiscoverer", score = 680, ageDivision = "SENIORS", avatarSeed = 24, isFriend = false, league = "Platinum"),
        LeaderboardEntry(username = "QuantumQuest", score = 510, ageDivision = "SENIORS", avatarSeed = 25, isFriend = true, league = "Gold")
    )
}

