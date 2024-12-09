package io.github.taalaydev.drivesafe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignMatchGame(
    onBackPressed: () -> Unit,
    onGameComplete: (score: Int) -> Unit
) {
    var currentScore by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(60) } // 60 seconds game
    var isGameActive by remember { mutableStateOf(true) }
    var currentRound by remember { mutableStateOf(1) }
    var streak by remember { mutableStateOf(0) }

    // Game state
    var selectedSign by remember { mutableStateOf<RoadSign?>(null) }
    var selectedMeaning by remember { mutableStateOf<String?>(null) }
    var feedbackState by remember { mutableStateOf<FeedbackState?>(null) }

    // Timer effect
    LaunchedEffect(isGameActive) {
        while (isGameActive && timeRemaining > 0) {
            delay(1000)
            timeRemaining--
            if (timeRemaining == 0) {
                isGameActive = false
                onGameComplete(currentScore)
            }
        }
    }

    // Current round signs and meanings
    val (currentSigns, currentMeanings) = remember(currentRound) {
        generateRoundData()
    }

    // Reset selections after delay
    if (isGameActive) {
        LaunchedEffect(feedbackState) {
            delay(1000)
            val isCorrect = selectedSign?.meaning == selectedMeaning

            if (isCorrect) {
                currentRound++
            }
            selectedSign = null
            selectedMeaning = null
            feedbackState = null
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Sign Match") },
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

                // Game Stats Bar
                GameStatsBar(
                    score = currentScore,
                    timeRemaining = timeRemaining,
                    streak = streak
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Signs Grid
            SignsGrid(
                signs = currentSigns,
                selectedSign = selectedSign,
                onSignSelected = { sign ->
                    if (feedbackState == null) {
                        selectedSign = if (selectedSign == sign) null else sign
                    }
                },
                feedbackState = feedbackState
            )

            // Meanings Grid
            MeaningsGrid(
                meanings = currentMeanings,
                selectedMeaning = selectedMeaning,
                onMeaningSelected = { meaning ->
                    if (feedbackState == null) {
                        selectedMeaning = if (selectedMeaning == meaning) null else meaning

                        // Check if both sign and meaning are selected
                        if (selectedSign != null && selectedMeaning != null) {
                            // Check if match is correct
                            val isCorrect = selectedSign?.meaning == meaning
                            feedbackState = if (isCorrect) {
                                currentScore += (10 + (streak * 2))
                                streak++
                                FeedbackState.CORRECT
                            } else {
                                streak = 0
                                FeedbackState.INCORRECT
                            }

                        }
                    }
                },
                feedbackState = feedbackState
            )
        }
    }
}

@Composable
private fun GameStatsBar(
    score: Int,
    timeRemaining: Int,
    streak: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameStat(
            icon = Icons.Default.Timer,
            value = timeRemaining.toString(),
            label = "sec"
        )
        GameStat(
            icon = Icons.Default.Star,
            value = score.toString(),
            label = "points"
        )
        GameStat(
            icon = Icons.Default.LocalFireDepartment,
            value = streak.toString(),
            label = "streak"
        )
    }
}

@Composable
private fun GameStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun SignsGrid(
    signs: List<RoadSign>,
    selectedSign: RoadSign?,
    onSignSelected: (RoadSign) -> Unit,
    feedbackState: FeedbackState?
) {
    Text(
        text = "Select a Sign",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        signs.forEach { sign ->
            SignCard(
                sign = sign,
                isSelected = sign == selectedSign,
                onSelected = { onSignSelected(sign) },
                feedbackState = if (sign == selectedSign) feedbackState else null
            )
        }
    }
}

@Composable
private fun MeaningsGrid(
    meanings: List<String>,
    selectedMeaning: String?,
    onMeaningSelected: (String) -> Unit,
    feedbackState: FeedbackState?
) {
    Text(
        text = "Select the Meaning",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meanings.forEach { meaning ->
            MeaningCard(
                meaning = meaning,
                isSelected = meaning == selectedMeaning,
                onSelected = { onMeaningSelected(meaning) },
                feedbackState = if (meaning == selectedMeaning) feedbackState else null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignCard(
    sign: RoadSign,
    isSelected: Boolean,
    onSelected: () -> Unit,
    feedbackState: FeedbackState?
) {
    Card(
        onClick = onSelected,
        modifier = Modifier.size(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                feedbackState == FeedbackState.CORRECT -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                feedbackState == FeedbackState.INCORRECT -> Color(0xFFFF5252).copy(alpha = 0.2f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = sign.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = when (feedbackState) {
                    FeedbackState.CORRECT -> Color(0xFF4CAF50)
                    FeedbackState.INCORRECT -> Color(0xFFFF5252)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeaningCard(
    meaning: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    feedbackState: FeedbackState?
) {
    Card(
        onClick = onSelected,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                feedbackState == FeedbackState.CORRECT -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                feedbackState == FeedbackState.INCORRECT -> Color(0xFFFF5252).copy(alpha = 0.2f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyLarge
            )
            if (feedbackState != null) {
                Icon(
                    imageVector = if (feedbackState == FeedbackState.CORRECT)
                        Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (feedbackState == FeedbackState.CORRECT)
                        Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
            }
        }
    }
}

data class RoadSign(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val meaning: String
)

enum class FeedbackState {
    CORRECT,
    INCORRECT
}

private fun generateRoundData(): Pair<List<RoadSign>, List<String>> {
    val signs = listOf(
        RoadSign("1", Icons.Default.Warning, "Warning: Dangerous curve ahead"),
        RoadSign("2", Icons.Default.DoNotDisturb, "No entry"),
        RoadSign("3", Icons.Default.Speed, "Speed limit 50"),
        RoadSign("4", Icons.Default.PriorityHigh, "Give way"),
        RoadSign("5", Icons.Default.Stop, "Stop"),
        RoadSign("6", Icons.Default.NoDrinks, "No parking"),
        RoadSign("7", Icons.Default.DirectionsRailway, "Railway crossing ahead"),
        RoadSign("8", Icons.Default.DirectionsCar, "Motor vehicles only")
    )

    // Select 3 random signs for this round
    val selectedSigns = signs.shuffled().take(3)
    val meanings = selectedSigns.map { it.meaning }.shuffled()

    return Pair(selectedSigns, meanings)
}