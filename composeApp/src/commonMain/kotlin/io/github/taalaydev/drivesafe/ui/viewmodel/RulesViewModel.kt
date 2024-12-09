package io.github.taalaydev.drivesafe.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.taalaydev.drivesafe.data.DataStorage
import io.github.taalaydev.drivesafe.domain.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RulesViewModel(
    private val dataStorage: DataStorage = DataStorage
) : ViewModel() {
    private val _lessons: MutableStateFlow<List<Lesson>> = MutableStateFlow(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            dataStorage.getLessonsForCategory(null).collect {
                _lessons.value = it
            }
        }
    }
}