package io.github.taalaydev.drivesafe.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.taalaydev.drivesafe.data.DataStorage
import io.github.taalaydev.drivesafe.domain.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RuleViewModel(
    private val lessonId: String,
    private val storage: DataStorage = DataStorage
) : ViewModel() {
    private val _lesson: MutableStateFlow<Lesson?> = MutableStateFlow(null)
    val lesson: StateFlow<Lesson?> = _lesson.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            storage.getLesson(lessonId).collect {
                _lesson.value = it
            }
        }
    }
}