package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun String.Companion.formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    val minutesString = if (minutes < 10) "0$minutes" else minutes.toString()
    val secondsString = if (remainingSeconds < 10) "0$remainingSeconds" else remainingSeconds.toString()

    return "$minutesString:$secondsString"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTestScreen(
    onBackPressed: () -> Unit,
    onFinishTest: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var timeRemaining by remember { mutableStateOf(15.minutes.inWholeSeconds) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Timer effect
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
        if (timeRemaining == 0L) {
            onFinishTest()
        }
    }

    val questions = remember {
        List(15) { index ->
            TestQuestion(
                id = index,
                text = "What should you do when approaching a yellow traffic light?",
                options = listOf(
                    "Speed up to get through",
                    "Prepare to stop if safe to do so",
                    "Always stop immediately",
                    "Ignore it if no other cars are present"
                ),
                correctAnswer = 1
            )
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Quick Test") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )

                // Timer and Progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = "${timeRemaining / 60}:${String.formatTime( timeRemaining % 60)}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Question Counter
                    Text(
                        text = "Question ${currentQuestionIndex + 1}/${questions.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Progress Indicator
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1) / questions.size.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color.White,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Navigator
            QuestionNavigator(
                totalQuestions = questions.size,
                currentQuestion = currentQuestionIndex,
                answeredQuestions = List(currentQuestionIndex) { true } + List(questions.size - currentQuestionIndex) { false },
                onQuestionSelected = { currentQuestionIndex = it }
            )

            // Question Card
            QuestionCard(
                question = questions[currentQuestionIndex],
                selectedAnswer = selectedAnswer,
                onAnswerSelected = { selectedAnswer = it }
            )

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentQuestionIndex > 0) {
                    OutlinedButton(
                        onClick = {
                            currentQuestionIndex--
                            selectedAnswer = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Previous")
                    }
                }

                Button(
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            selectedAnswer = null
                        } else {
                            onFinishTest()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (currentQuestionIndex < questions.size - 1) "Next" else "Finish")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (currentQuestionIndex < questions.size - 1)
                            Icons.Default.ArrowForward else Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionNavigator(
    totalQuestions: Int,
    currentQuestion: Int,
    answeredQuestions: List<Boolean>,
    onQuestionSelected: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(totalQuestions) { index ->
            QuestionDot(
                questionNumber = index + 1,
                isSelected = index == currentQuestion,
                isAnswered = answeredQuestions[index],
                onClick = { onQuestionSelected(index) }
            )
        }
    }
}

@Composable
private fun QuestionDot(
    questionNumber: Int,
    isSelected: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isAnswered -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = questionNumber.toString(),
            color = when {
                isSelected -> Color.White
                isAnswered -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuestionCard(
    question: TestQuestion,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            question.options.forEachIndexed { index, option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswer == index,
                    onClick = { onAnswerSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

data class TestQuestion(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int
)