package io.github.taalaydev.drivesafe.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficPuzzleGame(
    onBackPressed: () -> Unit,
    onGameComplete: (score: Int) -> Unit
) {
    var currentLevel by remember { mutableStateOf(1) }
    var currentScore by remember { mutableStateOf(0) }
    var movesCount by remember { mutableStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var isGameComplete by remember { mutableStateOf(false) }
    var gameState by remember { mutableStateOf<List<PuzzlePiece>>(emptyList()) }

    // Initialize game state with current puzzle
    LaunchedEffect(currentLevel) {
        gameState = generatePuzzle(currentLevel).pieces
    }

    var selectedPiece by remember { mutableStateOf<PuzzlePiece?>(null) }
    var startDragPosition by remember { mutableStateOf<Position?>(null) }

    val puzzle = remember(currentLevel) { generatePuzzle(currentLevel) }

    fun checkPuzzleComplete(): Boolean {
        // Find the red car (main piece)
        val redCar = gameState.find { it.id == "car1" }
        // Check if it reached the exit (for example, x >= 4)
        return redCar?.position?.x ?: 0f >= 4f
    }

    fun updatePiecePosition(piece: PuzzlePiece, newPosition: Position): Boolean {
        // Round the position to grid coordinates
        val roundedX = newPosition.x.roundToInt().toFloat()
        val roundedY = newPosition.y.roundToInt().toFloat()

        // Validate move
        if (!isValidMove(gameState, piece, Position(roundedX, roundedY))) {
            return false
        }

        // Update the piece position in game state
        gameState = gameState.map {
            if (it.id == piece.id) {
                it.copy(position = Position(roundedX, roundedY))
            } else {
                it
            }
        }

        return true
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                TopAppBar(
                    title = { Text("Traffic Puzzle") },
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

                GameStatsBar(
                    level = currentLevel,
                    score = currentScore,
                    moves = movesCount
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
            LevelDescription(puzzle = puzzle)

            PuzzleGrid(
                pieces = gameState,
                selectedPiece = selectedPiece,
                onPieceSelected = { piece ->
                    selectedPiece = piece
                    startDragPosition = piece.position
                },
                onPieceMoved = { piece, newPosition ->
                    if (updatePiecePosition(piece, newPosition)) {
                        if (startDragPosition != newPosition) {
                            movesCount++
                            if (checkPuzzleComplete()) {
                                currentScore += calculateScore(movesCount)
                                if (currentLevel < MAX_LEVELS) {
                                    currentLevel++
                                    movesCount = 0
                                } else {
                                    isGameComplete = true
                                }
                            }
                        }
                    }
                }
            )

            GameControls(
                onUndoClick = { /* Implement undo */ },
                onHintClick = { showHint = true },
                onResetClick = {
                    gameState = generatePuzzle(currentLevel).pieces
                    movesCount = 0
                }
            )
        }
    }

    if (showHint) {
        AlertDialog(
            onDismissRequest = { showHint = false },
            title = { Text("Hint") },
            text = { Text(puzzle.hint) },
            confirmButton = {
                TextButton(onClick = { showHint = false }) {
                    Text("Got it!")
                }
            }
        )
    }

    if (isGameComplete) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Congratulations!") },
            text = {
                Column {
                    Text("You've completed all levels!")
                    Text("Final Score: $currentScore")
                }
            },
            confirmButton = {
                TextButton(onClick = { onGameComplete(currentScore) }) {
                    Text("Finish")
                }
            }
        )
    }
}

@Composable
private fun GameStatsBar(
    level: Int,
    score: Int,
    moves: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameStat(
            icon = Icons.Default.Grade,
            value = level.toString(),
            label = "Level"
        )
        GameStat(
            icon = Icons.Default.Star,
            value = score.toString(),
            label = "Score"
        )
        GameStat(
            icon = Icons.Default.Compare,
            value = moves.toString(),
            label = "Moves"
        )
    }
}

@Composable
private fun GameStat(
    icon: ImageVector,
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
private fun LevelDescription(puzzle: Puzzle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = puzzle.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = puzzle.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PuzzleGrid(
    pieces: List<PuzzlePiece>,
    selectedPiece: PuzzlePiece?,
    onPieceSelected: (PuzzlePiece) -> Unit,
    onPieceMoved: (PuzzlePiece, Position) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        // Grid lines
        repeat(7) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .align(Alignment.TopStart)
                    .offset(y = (i * (100f/6)).dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .align(Alignment.TopStart)
                    .offset(x = (i * (100f/6)).dp)
            )
        }

        // Exit marker
        Box(
            modifier = Modifier
                .size((100f/6).dp)
                .align(Alignment.CenterEnd)
                .background(Color.Red.copy(alpha = 0.2f))
        )

        // Pieces
        pieces.forEach { piece ->
            PuzzlePiece(
                piece = piece,
                isSelected = piece == selectedPiece,
                onSelected = { onPieceSelected(piece) },
                onMoved = { newPosition -> onPieceMoved(piece, newPosition) }
            )
        }
    }
}

@Composable
private fun PuzzlePiece(
    piece: PuzzlePiece,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onMoved: (Position) -> Unit
) {
    Card(
        modifier = Modifier
            .size(48.dp)
            .offset(
                x = (piece.position.x * (100f/6)).dp,
                y = (piece.position.y * (100f/6)).dp
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onSelected() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Update position based on drag
                        val newX = (piece.position.x + dragAmount.x/(100f/6))
                        val newY = (piece.position.y + dragAmount.y/(100f/6))
                        // Ensure position is within grid bounds
                        val boundedX = newX.coerceIn(0f, 5f)
                        val boundedY = newY.coerceIn(0f, 5f)
                        onMoved(Position(boundedX, boundedY))
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                piece.color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = piece.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(piece.rotation)
            )
        }
    }
}

@Composable
private fun GameControls(
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = onUndoClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo"
            )
        }
        IconButton(
            onClick = onHintClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Hint"
            )
        }
        IconButton(
            onClick = onResetClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset"
            )
        }
    }
}

data class Position(val x: Float, val y: Float)
enum class Orientation {
    HORIZONTAL,
    VERTICAL
}

data class Size(val width: Int, val height: Int)

data class PuzzlePiece(
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val position: Position,
    val size: Size = Size(1, 2), // width, height in grid units
    val orientation: Orientation = Orientation.HORIZONTAL,
    val rotation: Float = 0f
)

data class Puzzle(
    val id: Int,
    val title: String,
    val description: String,
    val hint: String,
    val pieces: List<PuzzlePiece>,
    val isSolved: () -> Boolean
)

private fun generatePuzzle(level: Int): Puzzle {
    val pieces = when (level) {
        1 -> listOf(
            PuzzlePiece(
                id = "car1",
                icon = Icons.Default.DirectionsCar,
                color = Color.Red,
                position = Position(0f, 2f),
                size = Size(2, 1),
                orientation = Orientation.HORIZONTAL
            ),
            PuzzlePiece(
                id = "car2",
                icon = Icons.Default.DirectionsCar,
                color = Color.Blue,
                position = Position(2f, 2f),
                size = Size(2, 1),
                orientation = Orientation.HORIZONTAL
            ),
            PuzzlePiece(
                id = "truck1",
                icon = Icons.Default.LocalShipping,
                color = Color.Green,
                position = Position(2f, 3f),
                size = Size(1, 3),
                orientation = Orientation.VERTICAL
            )
        )
        // Add more levels with different configurations
        else -> listOf(
            PuzzlePiece(
                id = "car1",
                icon = Icons.Default.DirectionsCar,
                color = Color.Red,
                position = Position(0f, 2f),
                size = Size(2, 1),
                orientation = Orientation.HORIZONTAL
            )
        )
    }

    return Puzzle(
        id = level,
        title = "Level $level",
        description = when (level) {
            1 -> "Help the red car escape! Move the blocking vehicles out of the way."
            else -> "Clear the path for the red car to reach the exit."
        },
        hint = when (level) {
            1 -> "Try moving the blue car up to create space for the red car."
            else -> "Look for the vehicle blocking the direct path to the exit."
        },
        pieces = pieces,
        isSolved = { false }
    )
}


fun isValidMove(gameState: List<PuzzlePiece>, piece: PuzzlePiece, newPosition: Position): Boolean {
    // Check grid boundaries
    if (newPosition.x < 0 || newPosition.y < 0 ||
        newPosition.x + piece.size.width > 6 ||
        newPosition.y + piece.size.height > 6) {
        return false
    }

    // Check collision with other pieces
    val otherPieces = gameState.filter { it.id != piece.id }
    return !otherPieces.any { otherPiece ->
        checkCollision(
            Position(newPosition.x, newPosition.y),
            piece.size,
            otherPiece.position,
            otherPiece.size
        )
    }
}

fun checkCollision(pos1: Position, size1: Size, pos2: Position, size2: Size): Boolean {
    val x1 = pos1.x
    val y1 = pos1.y
    val x2 = pos2.x
    val y2 = pos2.y

    return !(x1 + size1.width <= x2 ||
            x2 + size2.width <= x1 ||
            y1 + size1.height <= y2 ||
            y2 + size2.height <= y1)
}

private fun calculateScore(moves: Int): Int {
    return maxOf(100 - (moves * 5), 10)
}

private const val MAX_LEVELS = 10