package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.taalaydev.drivesafe.domain.model.Lesson
import io.github.taalaydev.drivesafe.domain.model.Rule
import io.github.taalaydev.drivesafe.ui.components.ImportanceBadge
import io.github.taalaydev.drivesafe.ui.viewmodel.RuleViewModel


@Composable
fun RuleDetailScreen(
    lessonId: String,
    viewModel: RuleViewModel = viewModel { RuleViewModel(lessonId) },
    onBackPressed: () -> Unit,
    onShareClick: () -> Unit
) {
    val lesson = viewModel.lesson.collectAsState()

    if (lesson.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        RuleDetailContent(
            lesson = lesson.value!!,
            onBackPressed = onBackPressed,
            onShareClick = onShareClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleDetailContent(
    lesson: Lesson,
    onBackPressed: () -> Unit,
    onShareClick: () -> Unit,
) {
    var isBookmarked by remember { mutableStateOf(false) }
    var expandedRuleId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Поделиться",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { isBookmarked = !isBookmarked }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark
                                else Icons.Default.BookmarkBorder,
                                contentDescription = "Закладка",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = Color.White
                    )
                )

                LessonTitleSection(lesson = lesson)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                LessonSummaryCard(lesson = lesson)
            }

            items(lesson.rules) { rule ->
                RuleCard(
                    rule = rule,
                    isExpanded = expandedRuleId == rule.id,
                    onExpandClick = { expandedRuleId = if (expandedRuleId == rule.id) null else rule.id }
                )
            }

            item {
                PracticeQuestionCard()
            }
        }
    }
}

@Composable
private fun LessonTitleSection(lesson: Lesson) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ImportanceBadge(importance = lesson.importance)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = lesson.title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${lesson.duration} мин",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${lesson.rules.size} правил",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LessonSummaryCard(lesson: Lesson) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Краткое содержание",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RuleCard(
    rule: Rule,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onExpandClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rule.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = rule.reference,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = rule.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun PracticeQuestionCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Проверь себя",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Что означает мигающий зеленый сигнал светофора?",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val options = listOf(
                        "А) Запрещает движение",
                        "Б) Предупреждает о скорой смене сигнала",
                        "В) Разрешает движение без ограничений",
                        "Г) Требует немедленной остановки"
                    )

                    options.forEach { option ->
                        OutlinedButton(
                            onClick = { /* Обработка выбора */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Дополнительный класс состояния для экрана
@Stable
class RulesScreenState {
    var selectedCategoryId by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")
    var expandedLessonId by mutableStateOf<String?>(null)
    var bookmarkedLessons by mutableStateOf(setOf<String>())

    fun toggleBookmark(lessonId: String) {
        bookmarkedLessons = if (lessonId in bookmarkedLessons) {
            bookmarkedLessons - lessonId
        } else {
            bookmarkedLessons + lessonId
        }
    }

    fun filterLessons(lessons: List<Lesson>): List<Lesson> {
        return lessons.filter { lesson ->
            (selectedCategoryId == null || lesson.category == selectedCategoryId) &&
                    (searchQuery.isBlank() ||
                            lesson.title.contains(searchQuery, ignoreCase = true) ||
                            lesson.description.contains(searchQuery, ignoreCase = true))
        }
    }
}

// Вспомогательный класс для навигации
object RulesScreenDestination {
    const val route = "rules"
    const val lessonIdArg = "lessonId"

    fun createRoute(lessonId: String? = null): String {
        return if (lessonId != null) {
            "$route/$lessonId"
        } else {
            route
        }
    }
}