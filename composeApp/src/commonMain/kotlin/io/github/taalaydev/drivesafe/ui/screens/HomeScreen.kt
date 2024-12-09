package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onNavigateToRules: () -> Unit,
    onNavigateToSigns: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    Scaffold(
        topBar = { HomeTopAppBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProgressCard() }
            item { ModulesGrid(
                onNavigateToRules = onNavigateToRules,
                onNavigateToSigns = onNavigateToSigns,
                onNavigateToTests = onNavigateToTests,
                onNavigateToGames = onNavigateToGames
            ) }
            item { RecentActivityCard() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "DriveSafe Academy",
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700)
                )
                Text(
                    text = "1250 pts",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
private fun ProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = "Daily Progress",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = 0.7f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "70% of daily goals completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModulesGrid(
    onNavigateToRules: () -> Unit,
    onNavigateToSigns: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModuleCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.MenuBook,
                title = "Traffic Rules",
                description = "Learn rules & regulations",
                iconBackgroundColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1E88E5),
                onClick = onNavigateToRules
            )
            ModuleCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Traffic,
                title = "Road Signs",
                description = "Master traffic signs",
                iconBackgroundColor = Color(0xFFFFEBEE),
                iconTint = Color(0xFFE53935),
                onClick = onNavigateToSigns
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModuleCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Assignment,
                title = "Practice Tests",
                description = "Test your knowledge",
                iconBackgroundColor = Color(0xFFE8F5E9),
                iconTint = Color(0xFF43A047),
                onClick = onNavigateToTests
            )
            ModuleCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Games,
                title = "Mini Games",
                description = "Learn while playing",
                iconBackgroundColor = Color(0xFFF3E5F5),
                iconTint = Color(0xFF8E24AA),
                onClick = onNavigateToGames
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentActivityCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityItem(
                    icon = Icons.Default.Star,
                    text = "Completed Road Signs Quiz",
                    time = "2h ago",
                    iconTint = Color(0xFFFFD700)
                )
                ActivityItem(
                    icon = Icons.Default.EmojiEvents,
                    text = "New Achievement Unlocked",
                    time = "5h ago",
                    iconTint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(
    icon: ImageVector,
    text: String,
    time: String,
    iconTint: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(text = text)
            }
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}