package io.github.taalaydev.drivesafe.domain.model

import kotlinx.serialization.Serializable

enum class QuizDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

@Serializable
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: Int,
    val timeLimit: Int,
    val passingScore: Int,
    val group: String,
    val questions: List<QuizQuestion>
) {
    val difficultyEnum: QuizDifficulty
        get() = when (difficulty) {
            1 -> QuizDifficulty.EASY
            2 -> QuizDifficulty.MEDIUM
            3 -> QuizDifficulty.HARD
            4 -> QuizDifficulty.EXPERT
            else -> throw IllegalArgumentException("Unknown difficulty: $difficulty")
        }
}

@Serializable
data class QuizQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val category: String? = null,
    val imageUrl: String? = null,
    val points: Int = 1
)