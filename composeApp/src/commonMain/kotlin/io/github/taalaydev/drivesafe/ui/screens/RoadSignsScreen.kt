package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SignCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

data class RoadSign(
    val id: String,
    val name: String,
    val category: String,
    val type: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadSignsScreen(
    onBackPressed: () -> Unit,
    onSignSelected: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val primary = MaterialTheme.colorScheme.primary

    val categories = remember {
        listOf(
            SignCategory("all", "All Signs", Icons.Default.List, primary),
            SignCategory("warning", "Warning", Icons.Default.Warning, Color(0xFFFFA000)),
            SignCategory("mandatory", "Mandatory", Icons.Default.CheckCircle, Color(0xFF2196F3)),
            SignCategory("prohibitory", "Prohibitory", Icons.Default.Block, Color(0xFFE53935)),
            SignCategory("information", "Information", Icons.Default.Info, Color(0xFF4CAF50)),
            SignCategory("direction", "Direction", Icons.Default.Navigation, Color(0xFF9C27B0)),
            SignCategory("special", "Special", Icons.Default.Star, Color(0xFFE91E63))
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Road Signs") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle filter */ }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
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
                    placeholder = { Text("Search road signs...") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Categories
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category.id,
                        onSelected = { selectedCategory = category.id }
                    )
                }
            }

            // Recently Viewed
            RecentlyViewedSection()
            Spacer(modifier = Modifier.height(16.dp))
            // Signs Grid
            SignsGrid(onSignSelected = onSignSelected)
        }
    }
}

@Composable
private fun CategoryChip(
    category: SignCategory,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        onClick = onSelected,
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else category.color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = category.name,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SignsGrid(onSignSelected: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(6) { index ->
            SignCard(
                sign = RoadSign(
                    id = index.toString(),
                    name = "Sign ${index + 1}",
                    category = "Category",
                    type = "warning",
                    imageUrl = "/api/placeholder/150/150"
                ),
                onClick = { onSignSelected(index.toString()) }
            )
        }
    }
}

@Composable
private fun SignCard(
    sign: RoadSign,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (sign.type) {
                "warning" -> Color(0xFFFFF3E0)
                "mandatory" -> Color(0xFFE3F2FD)
                "prohibitory" -> Color(0xFFFFEBEE)
                "information" -> Color(0xFFE8F5E9)
                "direction" -> Color(0xFFF3E5F5)
                else -> Color(0xFFFCE4EC)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sign.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = sign.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentlyViewedSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Recently Viewed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { index ->
                MiniSignCard(
                    sign = RoadSign(
                        id = "recent_$index",
                        name = "Recent Sign ${index + 1}",
                        category = "Category",
                        type = "warning",
                        imageUrl = "/api/placeholder/80/80"
                    )
                )
            }
        }
    }
}

@Composable
private fun MiniSignCard(sign: RoadSign) {
    Card(
        modifier = Modifier.width(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = sign.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

// Helper functions for managing sign data
object SignUtils {
    fun getSignTypeColor(type: String): Color {
        return when (type) {
            "warning" -> Color(0xFFFFA000)
            "mandatory" -> Color(0xFF2196F3)
            "prohibitory" -> Color(0xFFE53935)
            "information" -> Color(0xFF4CAF50)
            "direction" -> Color(0xFF9C27B0)
            "special" -> Color(0xFFE91E63)
            else -> Color(0xFF757575)
        }
    }

    fun getSignTypeIcon(type: String): ImageVector {
        return when (type) {
            "warning" -> Icons.Default.Warning
            "mandatory" -> Icons.Default.CheckCircle
            "prohibitory" -> Icons.Default.Block
            "information" -> Icons.Default.Info
            "direction" -> Icons.Default.Navigation
            "special" -> Icons.Default.Star
            else -> Icons.Default.Help
        }
    }
}

// Extension to handle sign filtering
fun List<RoadSign>.filterByCategory(category: String): List<RoadSign> {
    return if (category == "all") {
        this
    } else {
        filter { it.type == category }
    }
}

fun List<RoadSign>.filterBySearch(query: String): List<RoadSign> {
    return if (query.isBlank()) {
        this
    } else {
        filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }
}

// State holder for the screen
@Stable
class RoadSignsScreenState {
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("all")
    var recentlyViewedSigns by mutableStateOf(listOf<RoadSign>())

    fun updateSearch(query: String) {
        searchQuery = query
    }

    fun updateCategory(category: String) {
        selectedCategory = category
    }

    fun addToRecentlyViewed(sign: RoadSign) {
        recentlyViewedSigns = (listOf(sign) + recentlyViewedSigns)
            .distinctBy { it.id }
            .take(5)
    }
}