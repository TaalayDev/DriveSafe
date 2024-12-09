package io.github.taalaydev.drivesafe.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.taalaydev.drivesafe.data.DataStorage
import io.github.taalaydev.drivesafe.data.DownloadManager
import io.github.taalaydev.drivesafe.data.DownloadResult
import io.github.taalaydev.drivesafe.domain.model.Lesson
import io.github.taalaydev.drivesafe.domain.model.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SplashViewModel(
    private val downloadManager: DownloadManager = DownloadManager,
    private val storage: DataStorage = DataStorage
) : ViewModel() {
    private val dataUrl = "https://taalaydev.github.io/drivesafe/pdd_ky.json"

    private val _state = MutableStateFlow<SplashState>(SplashState.CheckingDatabase)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        start()
    }

    private suspend fun startDownload() {
        try {
            downloadManager.downloadWithProgress(dataUrl).collect { result ->
                when (result) {
                    is DownloadResult.Success -> {
                        val jsonString = result.data.decodeToString()
                        Json.decodeFromString<DownloadData>(jsonString).let {
                            storage.updateData(it.rules)
                            storage.updateQuizzes(it.quizes)
                        }

                        _state.value = SplashState.NavigateNext
                    }
                    is DownloadResult.Loading -> {
                        _state.value = SplashState.Downloading(result.progress.percent)
                    }
                    is DownloadResult.Error -> {
                        _state.value = SplashState.Error("Failed to download data")
                    }
                }
            }
        } catch (e: Exception) {
            println("Failed to download data: $e")
            _state.value = SplashState.Error("Failed to download data")
        }
    }

    private fun downloadInBackGround() {
        viewModelScope.launch {
            try {
                downloadManager.downloadFile<DownloadData>(dataUrl).let { result ->
                    when (result) {
                        is DownloadResult.Success -> {
                            storage.updateData(result.data.rules)
                            storage.updateQuizzes(result.data.quizes)
                        }
                        is DownloadResult.Error -> {
                            println("Failed to download data: ${result.message}")
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                println("Failed to download data: $e")
            }
        }
    }

    private fun start() {
        viewModelScope.launch {
            _state.value = SplashState.CheckingDatabase
            if (storage.hasData()) {
                startDownload()
            } else {
                _state.value = SplashState.NavigateNext

                downloadInBackGround()
            }
        }

    }
}

sealed class SplashState {
    data object CheckingDatabase : SplashState()
    data class Downloading(val progress: Float) : SplashState()
    data object NavigateNext : SplashState()
    data class Error(val message: String) : SplashState()
}

@Serializable
data class DownloadData(
    val rules: List<Lesson>,
    val quizes: List<Quiz>
)