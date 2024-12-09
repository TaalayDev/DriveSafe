package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGamesScreen(
    onBackPressed: () -> Unit,
    onGameSelected: (String) -> Unit
) {
    val games = remember {
        listOf(
            Game(
                id = "sign_match",
                title = "Sign Match",
                description = "Match road signs with their meanings",
                icon = Icons.Default.Traffic,
                color = Color(0xFFF59E0B),
                difficulty = "Easy",
                timeEstimate = "5 min",
                points = 100,
                progress = 65
            ),
            Game(
                id = "traffic_puzzle",
                title = "Traffic Puzzle",
                description = "Solve traffic scenarios",
                icon = Icons.Default.Extension,
                color = Color(0xFF3B82F6),
                difficulty = "Medium",
                timeEstimate = "10 min",
                points = 150,
                progress = 40
            ),
            Game(
                id = "speed_quiz",
                title = "Speed Quiz",
                description = "Quick questions against the clock",
                icon = Icons.Default.Timer,
                color = Color(0xFFEC4899),
                difficulty = "Hard",
                timeEstimate = "3 min",
                points = 200,
                progress = 80
            )
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Mini Games") },
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

                // Stats Overview
                StatsOverview(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Featured Game
            item {
                FeaturedGameCard(
                    game = games[0],
                    onPlayClick = { onGameSelected(games[0].id) }
                )
            }

            // Daily Challenge
            item {
                DailyChallengeCard(
                    onPlayClick = { onGameSelected("daily_challenge") }
                )
            }

            // All Games Section
            item {
                Text(
                    text = "All Games",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Game Cards
            items(games) { game ->
                GameCard(
                    game = game,
                    onPlayClick = { onGameSelected(game.id) }
                )
            }

            // Achievements Preview
            item {
                AchievementsPreview()
            }
        }
    }
}

@Composable
private fun StatsOverview(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "1,250",
                label = "Points",
                icon = Icons.Default.Star,
                iconTint = Color(0xFFFFD700)
            )
            StatItem(
                value = "15",
                label = "Games Played",
                icon = Icons.Default.Games,
                iconTint = Color(0xFF60A5FA)
            )
            StatItem(
                value = "5",
                label = "Achievements",
                icon = Icons.Default.EmojiEvents,
                iconTint = Color(0xFFA855F7)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    iconTint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            color = Color.White,
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
private fun FeaturedGameCard(
    game: Game,
    onPlayClick: () -> Unit
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
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Featured Game",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${game.points} pts",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = game.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = game.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = game.description,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play Now")
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Daily Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "New challenge available!",
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }
            FilledTonalButton(
                onClick = onPlayClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text("Play")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameCard(
    game: Game,
    onPlayClick: () -> Unit
) {
    Card(
        onClick = onPlayClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = game.color.copy(alpha = 0.1f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = game.icon,
                            contentDescription = null,
                            tint = game.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GameTag(text = game.difficulty)
                        GameTag(text = game.timeEstimate)
                        GameTag(text = "${game.points} pts")
                    }
                }
            }
        }
    }
}

@Composable
private fun GameTag(
    text: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AchievementsPreview() {
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
                    text = "Recent Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { /* View all */ }) {
                    Text("View All")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(3) { index ->
                    AchievementItem(
                        icon = when (index) {
                            0 -> Icons.Default.Speed
                            1 -> Icons.Default.Psychology
                            else -> Icons.Default.LocalPolice
                        },
                        title = when (index) {
                            0 -> "Speed Demon"
                            1 -> "Quick Thinker"
                            else -> "Rule Master"
                        },
                        points = (index + 1) * 100
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    icon: ImageVector,
    title: String,
    points: Int
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$points pts",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

data class Game(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val difficulty: String,
    val timeEstimate: String,
    val points: Int,
    val progress: Int
)