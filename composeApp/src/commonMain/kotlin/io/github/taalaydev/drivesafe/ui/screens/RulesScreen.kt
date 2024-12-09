package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.taalaydev.drivesafe.domain.model.Lesson
import io.github.taalaydev.drivesafe.domain.model.LessonCategory
import io.github.taalaydev.drivesafe.domain.model.LessonImportance
import io.github.taalaydev.drivesafe.ui.components.ImportanceBadge
import io.github.taalaydev.drivesafe.ui.viewmodel.RulesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    viewModel: RulesViewModel = viewModel { RulesViewModel() },
    onBackPressed: () -> Unit,
    onLessonSelected: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val lessons by viewModel.lessons.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Traffic Rules") },
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

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search rules...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                lessons.firstOrNull()?.let {
                    FeaturedLessonCard(
                        lesson = it,
                        onLessonClick = onLessonSelected
                    )
                }
            }

            items(
                lessons.filter { lesson ->
                    if (searchQuery.isBlank()) true
                    else lesson.title.contains(searchQuery, ignoreCase = true) ||
                            lesson.description.contains(searchQuery, ignoreCase = true)
                }
            ) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = { onLessonSelected(lesson.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    categories: List<LessonCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category.id == selectedCategory,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.title) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun FeaturedLessonCard(
    lesson: Lesson,
    onLessonClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLessonClick(lesson.id) }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ImportanceBadge(importance = lesson.importance)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        lesson.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        lesson.description,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonCard(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ImportanceBadge(importance = lesson.importance)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${lesson.duration} мин • ${lesson.rules.size} правил",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

