package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeTestsScreen(
    onBackPressed: () -> Unit,
    onStartTest: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Practice Tests") },
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

                // Progress Overview
                ProgressOverview(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Test
            item {
                QuickTestCard(
                    onStartQuickTest = { onStartTest("quick") }
                )
            }

            // Test Categories
            item {
                TestCategoriesGrid(
                    onCategorySelected = { category ->
                        onStartTest(category)
                    }
                )
            }

            // Recent Results
            item {
                Text(
                    text = "Recent Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(getRecentResults()) { result ->
                ResultCard(result = result)
            }
        }
    }
}

@Composable
private fun ProgressOverview(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Progress",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Keep up the good work!",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat("Tests Taken", "12")
                ProgressStat("Avg. Score", "85%")
                ProgressStat("Complete", "65%")
            }
        }
    }
}

@Composable
private fun ProgressStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun QuickTestCard(
    onStartQuickTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quick Test",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "15 questions • 15 minutes",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartQuickTest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Now")
            }
        }
    }
}

@Composable
private fun TestCategoriesGrid(
    onCategorySelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestCategoryCard(
                modifier = Modifier.weight(1f),
                title = "Chapter Tests",
                description = "Practice by topics",
                icon = Icons.Default.MenuBook,
                color = Color(0xFF4CAF50),
                progress = 75,
                onClick = { onCategorySelected("chapter") }
            )
            TestCategoryCard(
                modifier = Modifier.weight(1f),
                title = "Mock Exam",
                description = "Full test simulation",
                icon = Icons.Default.Assignment,
                color = Color(0xFF9C27B0),
                progress = 40,
                onClick = { onCategorySelected("mock") }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestCategoryCard(
                modifier = Modifier.weight(1f),
                title = "Daily Challenge",
                description = "New questions daily",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFFFF9800),
                progress = 90,
                onClick = { onCategorySelected("daily") }
            )
            TestCategoryCard(
                modifier = Modifier.weight(1f),
                title = "Custom Test",
                description = "Create your own",
                icon = Icons.Default.Add,
                color = Color(0xFFE91E63),
                progress = 0,
                onClick = { onCategorySelected("custom") }
            )
        }
    }
}

@Composable
private fun TestCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    progress: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (progress > 0) {
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$progress%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = color
            )
        }
    }
}

data class TestResult(
    val id: String,
    val title: String,
    val score: Int,
    val date: String,
    val isPassed: Boolean
)

@Composable
private fun ResultCard(result: TestResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (result.isPassed) {
                        MaterialTheme.success.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    }
                ) {
                    Icon(
                        imageVector = if (result.isPassed) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = if (result.isPassed) {
                            MaterialTheme.success
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = result.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (result.score >= 80) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${result.score}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Helper function to generate sample test results
private fun getRecentResults(): List<TestResult> {
    return listOf(
        TestResult(
            id = "1",
            title = "Chapter 3: Right of Way",
            score = 92,
            date = "Today",
            isPassed = true
        ),
        TestResult(
            id = "2",
            title = "Mock Test #4",
            score = 88,
            date = "Yesterday",
            isPassed = true
        ),
        TestResult(
            id = "3",
            title = "Road Signs Quiz",
            score = 65,
            date = "2 days ago",
            isPassed = false
        )
    )
}

// Extension for the color scheme

private val MaterialTheme.success: Color
    @Composable get() = Color(0xFF4CAF50)

// State holder for the screen
@Stable
class PracticeTestsScreenState {
    var selectedTestType by mutableStateOf<String?>(null)
    var recentResults by mutableStateOf<List<TestResult>>(emptyList())
    var testProgress by mutableStateOf<Map<String, Int>>(emptyMap())

    fun updateTestProgress(testType: String, progress: Int) {
        testProgress = testProgress + (testType to progress)
    }

    fun addTestResult(result: TestResult) {
        recentResults = (listOf(result) + recentResults).take(5)
    }

    fun getTestProgress(testType: String): Int {
        return testProgress[testType] ?: 0
    }
}