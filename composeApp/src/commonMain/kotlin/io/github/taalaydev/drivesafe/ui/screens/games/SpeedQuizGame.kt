package io.github.taalaydev.drivesafe.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int
)

sealed interface GameState {
    data object Idle : GameState
    data class Playing(
        val currentQuestionIndex: Int,
        val score: Int,
        val streak: Int,
        val timeLeft: Int
    ) : GameState
    data class Finished(
        val finalScore: Int,
        val bestStreak: Int
    ) : GameState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedQuizGame(
    onBackPressed: () -> Unit
) {
    var gameState: GameState by remember { mutableStateOf(GameState.Idle) }
    var bestStreak by remember { mutableStateOf(0) }

    val questions = remember {
        listOf(
            Question(
                "What does a yellow traffic light mean?",
                listOf(
                    "Stop immediately",
                    "Prepare to stop safely",
                    "Speed up to clear intersection",
                    "Ignore it if you're turning right"
                ),
                1
            ),
            Question(
                "When should you use hazard lights?",
                listOf(
                    "When parallel parking",
                    "When driving slowly",
                    "During an emergency/breakdown",
                    "In heavy rain"
                ),
                2
            ),
            Question(
                "What's the speed limit in a school zone?",
                listOf(
                    "15 mph",
                    "20 mph",
                    "25 mph",
                    "30 mph"
                ),
                1
            )
        )
    }

    LaunchedEffect(gameState) {
        if (gameState is GameState.Playing) {
            while (true) {
                delay(1000)
                gameState = when (val currentState = gameState) {
                    is GameState.Playing -> {
                        if (currentState.timeLeft > 0) {
                            currentState.copy(timeLeft = currentState.timeLeft - 1)
                        } else {
                            handleAnswerSelection(
                                questions = questions,
                                currentState = currentState,
                                selectedAnswer = -1,
                                bestStreak = bestStreak,
                                onStateUpdate = { newState, newBestStreak ->
                                    bestStreak = newBestStreak
                                    newState
                                }
                            )
                        }
                    }
                    else -> break
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speed Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = gameState) {
                GameState.Idle -> IdleGameState(
                    onStartClick = {
                        gameState = GameState.Playing(
                            currentQuestionIndex = 0,
                            score = 0,
                            streak = 0,
                            timeLeft = 10
                        )
                    }
                )
                is GameState.Playing -> PlayingGameState(
                    state = state,
                    question = questions[state.currentQuestionIndex],
                    onAnswerSelected = { selectedAnswer ->
                        gameState = handleAnswerSelection(
                            questions = questions,
                            currentState = state,
                            selectedAnswer = selectedAnswer,
                            bestStreak = bestStreak,
                            onStateUpdate = { newState, newBestStreak ->
                                bestStreak = newBestStreak
                                newState
                            }
                        )
                    }
                )
                is GameState.Finished -> FinishedGameState(
                    state = state,
                    onPlayAgainClick = {
                        gameState = GameState.Playing(
                            currentQuestionIndex = 0,
                            score = 0,
                            streak = 0,
                            timeLeft = 10
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun IdleGameState(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Speed Quiz Challenge",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Test your knowledge under pressure!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("10s per question")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bonus points for speed")
            }
        }

        Button(
            onClick = onStartClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Start Quiz")
        }
    }
}

@Composable
private fun PlayingGameState(
    state: GameState.Playing,
    question: Question,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Question ${state.currentQuestionIndex + 1}/3",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.score}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (state.timeLeft <= 3) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${state.timeLeft}s",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (state.timeLeft <= 3) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.titleLarge
                )

                question.options.forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = { onAnswerSelected(index) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Streak: ${state.streak}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun FinishedGameState(
    state: GameState.Finished,
    onPlayAgainClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Quiz Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Final Score: ${state.finalScore}",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Best Streak: ${state.bestStreak}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Play Again")
            }
        }
    }
}

private fun handleAnswerSelection(
    questions: List<Question>,
    currentState: GameState.Playing,
    selectedAnswer: Int,
    bestStreak: Int,
    onStateUpdate: (GameState, Int) -> GameState
): GameState {
    val isCorrect = selectedAnswer == questions[currentState.currentQuestionIndex].correctAnswer
    val newScore = currentState.score + if (isCorrect) currentState.timeLeft * 10 else 0
    val newStreak = if (isCorrect) currentState.streak + 1 else 0
    val newBestStreak = maxOf(bestStreak, newStreak)

    return if (currentState.currentQuestionIndex < questions.size - 1) {
        onStateUpdate(
            GameState.Playing(
                currentQuestionIndex = currentState.currentQuestionIndex + 1,
                score = newScore,
                streak = newStreak,
                timeLeft = 10
            ),
            newBestStreak
        )
    } else {
        onStateUpdate(
            GameState.Finished(
                finalScore = newScore,
                bestStreak = newBestStreak
            ),
            newBestStreak
        )
    }
}