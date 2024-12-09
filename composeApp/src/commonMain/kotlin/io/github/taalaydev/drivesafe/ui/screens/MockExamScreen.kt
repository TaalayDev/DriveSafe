package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class ExamQuestion(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

sealed interface ExamState {
    data object Introduction : ExamState
    data class InProgress(
        val currentQuestionIndex: Int = 0,
        val timeRemaining: Int = 45 * 60, // 45 minutes in seconds
        val answers: Map<Int, Int> = emptyMap(),
        val flaggedQuestions: Set<Int> = emptySet()
    ) : ExamState
    data class Review(
        val answers: Map<Int, Int>,
        val score: Int,
        val timeTaken: Int,
        val isPassed: Boolean
    ) : ExamState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockExamScreen(
    onBackPressed: () -> Unit
) {
    var examState: ExamState by remember { mutableStateOf(ExamState.Introduction) }

    val questions = remember {
        List(30) { index ->
            ExamQuestion(
                id = index,
                text = "Sample question ${index + 1} about traffic rules and regulations?",
                options = List(4) { "Option ${it + 1} for question ${index + 1}" },
                correctAnswer = 0,
                explanation = "Explanation for question ${index + 1}"
            )
        }
    }

    // Timer for exam
    LaunchedEffect(examState) {
        if (examState is ExamState.InProgress) {
            while (true) {
                delay(1.seconds)
                examState = when (val currentState = examState) {
                    is ExamState.InProgress -> {
                        if (currentState.timeRemaining > 0) {
                            currentState.copy(timeRemaining = currentState.timeRemaining - 1)
                        } else {
                            // Time's up - submit exam
                            submitExam(currentState, questions)
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
                title = { Text("Mock Exam") },
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
                ),
                actions = {
                    if (examState is ExamState.InProgress) {
                        TextButton(
                            onClick = {
                                examState = submitExam(examState as ExamState.InProgress, questions)
                            }
                        ) {
                            Text(
                                "Submit",
                                color = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val state = examState) {
                ExamState.Introduction -> IntroductionSection(
                    onStartExam = {
                        examState = ExamState.InProgress()
                    }
                )
                is ExamState.InProgress -> ExamSection(
                    state = state,
                    questions = questions,
                    onAnswerSelected = { questionId, answerId ->
                        examState = state.copy(
                            answers = state.answers + (questionId to answerId)
                        )
                    },
                    onQuestionFlagged = { questionId ->
                        examState = state.copy(
                            flaggedQuestions = if (state.flaggedQuestions.contains(questionId)) {
                                state.flaggedQuestions - questionId
                            } else {
                                state.flaggedQuestions + questionId
                            }
                        )
                    },
                    onNavigateToQuestion = { questionIndex ->
                        examState = state.copy(currentQuestionIndex = questionIndex)
                    }
                )
                is ExamState.Review -> ReviewSection(
                    state = state,
                    questions = questions,
                    onRetakeExam = {
                        examState = ExamState.InProgress()
                    }
                )
            }
        }
    }
}

@Composable
private fun IntroductionSection(
    onStartExam: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Mock Driving Test",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExamInfoRow(
                    icon = Icons.Default.Timer,
                    title = "Duration",
                    description = "45 minutes"
                )
                ExamInfoRow(
                    icon = Icons.Default.QuestionAnswer,
                    title = "Questions",
                    description = "30 multiple choice"
                )
                ExamInfoRow(
                    icon = Icons.Default.Grade,
                    title = "Passing Score",
                    description = "80% (24 correct answers)"
                )
                ExamInfoRow(
                    icon = Icons.Default.Info,
                    title = "Note",
                    description = "You can flag questions to review later"
                )
            }
        }

        Button(
            onClick = onStartExam,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Start Exam")
        }
    }
}

@Composable
private fun ExamSection(
    state: ExamState.InProgress,
    questions: List<ExamQuestion>,
    onAnswerSelected: (Int, Int) -> Unit,
    onQuestionFlagged: (Int) -> Unit,
    onNavigateToQuestion: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar with timer and progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentQuestionIndex + 1}/${questions.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (state.timeRemaining < 300) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatTime(state.timeRemaining),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.timeRemaining < 300) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Question navigator
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    questions.forEachIndexed { index, _ ->
                        QuestionIndicator(
                            questionIndex = index,
                            isSelected = index == state.currentQuestionIndex,
                            isAnswered = state.answers.containsKey(index),
                            isFlagged = state.flaggedQuestions.contains(index),
                            onClick = { onNavigateToQuestion(index) }
                        )
                    }
                }
            }
        }

        // Current question
        val currentQuestion = questions[state.currentQuestionIndex]
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = { onQuestionFlagged(currentQuestion.id) }
                    ) {
                        Icon(
                            imageVector = if (state.flaggedQuestions.contains(currentQuestion.id)) {
                                Icons.Default.Flag
                            } else {
                                Icons.Default.OutlinedFlag
                            },
                            contentDescription = "Flag question",
                            tint = if (state.flaggedQuestions.contains(currentQuestion.id)) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = state.answers[currentQuestion.id] == index
                    OutlinedButton(
                        onClick = { onAnswerSelected(currentQuestion.id, index) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(8.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { onNavigateToQuestion(maxOf(0, state.currentQuestionIndex - 1)) },
                enabled = state.currentQuestionIndex > 0
            ) {
                Icon(Icons.Default.ArrowBack, null)
                Spacer(Modifier.width(4.dp))
                Text("Previous")
            }

            TextButton(
                onClick = { onNavigateToQuestion(minOf(questions.lastIndex, state.currentQuestionIndex + 1)) },
                enabled = state.currentQuestionIndex < questions.lastIndex
            ) {
                Text("Next")
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
        }
    }
}

@Composable
private fun ReviewSection(
    state: ExamState.Review,
    questions: List<ExamQuestion>,
    onRetakeExam: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = if (state.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = if (state.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Text(
            text = if (state.isPassed) "Congratulations!" else "Keep Practicing",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Exam Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                ResultRow(
                    icon = Icons.Default.Score,
                    title = "Score",
                    value = "${state.score}/${questions.size} (${(state.score * 100f / questions.size).toInt()}%)"
                )

                ResultRow(
                    icon = Icons.Default.Timer,
                    title = "Time Taken",
                    value = formatTime(state.timeTaken)
                )

                ResultRow(
                    icon = if (state.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    title = "Status",
                    value = if (state.isPassed) "Passed" else "Failed"
                )
            }
        }

        Button(
            onClick = onRetakeExam,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retake Exam")
        }
    }
}

@Composable
private fun ExamInfoRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuestionIndicator(
    questionIndex: Int,
    isSelected: Boolean,
    isAnswered: Boolean,
    isFlagged: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isAnswered -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isFlagged) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = (questionIndex + 1).toString(),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else if (isAnswered) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return "$minutes:$remainingSeconds"
}

private fun submitExam(
    state: ExamState.InProgress,
    questions: List<ExamQuestion>
): ExamState.Review {
    val score = state.answers.count { (questionId, answerId) ->
        answerId == questions[questionId].correctAnswer
    }
    val timeTaken = 45 * 60 - state.timeRemaining
    val isPassed = score >= 24 // 80% of 30 questions

    return ExamState.Review(
        answers = state.answers,
        score = score,
        timeTaken = timeTaken,
        isPassed = isPassed
    )
}

// Add to your Destination.kt
sealed class ExamMode {
    object Practice : ExamMode()
    object Mock : ExamMode()

    fun toRoute(): String = when (this) {
        is Practice -> "practice"
        is Mock -> "mock"
    }

    companion object {
        fun fromRoute(route: String?): ExamMode = when (route) {
            "practice" -> Practice
            "mock" -> Mock
            else -> Practice
        }
    }
}