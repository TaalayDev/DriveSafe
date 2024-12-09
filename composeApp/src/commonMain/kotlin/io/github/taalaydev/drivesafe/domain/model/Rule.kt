package io.github.taalaydev.drivesafe.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class LessonImportance {
    ESSENTIAL,
    IMPORTANT,
    BASIC
}

@Serializable
data class Rule(
    val id: String,
    val title: String,
    val content: String,
    val reference: String, // ссылка на номер в ПДД
    val imageUrl: String? = null
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val importance: LessonImportance,
    val duration: Int, // в минутах
    val rules: List<Rule>
)

@Serializable
data class LessonCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String
)

@Serializable
sealed class LessonProgress {
    data class NotStarted(val lessonId: String) : LessonProgress()
    data class InProgress(
        val lessonId: String,
        val completedRules: List<String>,
        val lastAccessTime: Long
    ) : LessonProgress()
    data class Completed(
        val lessonId: String,
        val completionTime: Long,
        val score: Int? = null
    ) : LessonProgress()
}