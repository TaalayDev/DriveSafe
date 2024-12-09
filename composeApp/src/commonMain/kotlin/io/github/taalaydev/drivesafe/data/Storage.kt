package io.github.taalaydev.drivesafe.data

import io.github.taalaydev.drivesafe.domain.model.*
import io.github.taalaydev.drivesafe.getStorageFilePath
import kotlinx.io.files.Path
import io.github.xxfast.kstore.*
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)

@Serializable
data class AppState(
    val completedLessons: List<String> = emptyList(),
    val bookmarkedLessons: List<String> = emptyList(),
    val testScores: Map<String, Int> = emptyMap(),
    val gameProgress: Map<String, Int> = emptyMap(),
    val userPreferences: UserPreferences = UserPreferences()
)

object AppStateStorage {
    private val store: KStore<AppState> = storeOf(
        file = Path(path = getStorageFilePath() + "/state.json"),
        default = AppState()
    )

    val updates: Flow<AppState> = store.updates.map { it ?: AppState() }
    suspend fun getState(): AppState = store.get() ?: AppState()

    suspend fun updateState(transform: (AppState?) -> AppState?) {
        store.update(transform)
    }

    suspend fun addCompletedLesson(lessonId: String) {
        updateState { state ->
            state?.copy(
                completedLessons = (state.completedLessons + lessonId).distinct()
            )
        }
    }

    suspend fun toggleBookmark(lessonId: String) {
        updateState { state ->
            val state = state ?: AppState()
            val bookmarks = if (lessonId in state.bookmarkedLessons) {
                state.bookmarkedLessons - lessonId
            } else {
                state.bookmarkedLessons + lessonId
            }
            state.copy(bookmarkedLessons = bookmarks)
        }
    }

    suspend fun saveTestScore(testId: String, score: Int) {
        updateState { state ->
            state?.copy(
                testScores = state.testScores + (testId to score)
            )
        }
    }

    suspend fun updateGameProgress(gameId: String, progress: Int) {
        updateState { state ->
            state?.copy(
                gameProgress = state.gameProgress + (gameId to progress)
            )
        }
    }

    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        updateState { state ->
            state?.copy(userPreferences = transform(state.userPreferences))
        }
    }
}


object DataStorage {
    private var store: KStore<List<Lesson>> = storeOf(
        file = Path(path = getStorageFilePath() + "/lessons.json"),
        default = emptyList()
    )
    private var quizStore: KStore<List<Quiz>> = storeOf(
        file = Path(path = getStorageFilePath() + "/quizzes.json"),
        default = emptyList()
    )

    suspend fun updateData(data: List<Lesson>) {
        store.update { data }
    }

    suspend fun updateQuizzes(data: List<Quiz>) {
        quizStore.update { data }
    }

    suspend fun hasData(): Boolean {
        return store.get() != null && store.get()!!.isNotEmpty()
    }

    fun getLessonsForCategory(categoryId: String?): Flow<List<Lesson>> {
        return store.updates.map { lessons ->
            val data = lessons ?: emptyList()
            if (categoryId == null) {
                data
            } else {
                data.filter { it.category == categoryId }
            }
        }
    }

    fun getLesson(lessonId: String): Flow<Lesson?> {
        return store.updates.map { lessons ->
            lessons?.find { it.id == lessonId }
        }
    }

    fun getQuizzes(): Flow<List<Quiz>> {
        return quizStore.updates.map { it ?: emptyList() }
    }

    fun getQuiz(quizId: String): Flow<Quiz?> {
        return quizStore.updates.map { quizzes ->
            quizzes?.find { it.id == quizId }
        }
    }
}

