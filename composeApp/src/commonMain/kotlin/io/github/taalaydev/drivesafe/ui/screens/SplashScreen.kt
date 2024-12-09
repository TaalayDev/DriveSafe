package io.github.taalaydev.drivesafe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.taalaydev.drivesafe.ui.viewmodel.SplashState
import io.github.taalaydev.drivesafe.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel { SplashViewModel() },
    onSplashFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var readyToNavigate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(500)
        showContent = true
        delay(2000)
        readyToNavigate = true
        if (state is SplashState.NavigateNext) {
            onSplashFinished()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            SplashState.NavigateNext -> {
                if (readyToNavigate) {
                    onSplashFinished()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3),
                        Color(0xFF1976D2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Animation
            val scale by animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0.5f,
                animationSpec = tween(1000, easing = EaseOutBack)
            )

            Box(
                modifier = Modifier.scale(scale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = Color.White
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(90f),
                    tint = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text Animations
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(1000)
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DriveSafe Academy",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your Journey to Safe Driving Starts Here",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp
                    )

                    when (state) {
                        is SplashState.CheckingDatabase -> {
                            CircularProgressIndicator(color = Color.White)
                        }

                        is SplashState.Downloading -> {
                            val progress = (state as SplashState.Downloading).progress
                            CircularProgressIndicator(
                                progress = { progress },
                                color = Color.White
                            )
                        }

                        is SplashState.Error -> {
                            val message = (state as SplashState.Error).message
                            Text(
                                text = message,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Bars Animation
            if (showContent) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        LoadingBar(delay = index * 200L)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingBar(delay: Long) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        startAnimation = true
    }

    val width by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutQuad)
    )

    Box(
        modifier = Modifier
            .width(64.dp * width)
            .height(4.dp)
            .clip(CircleShape)
            .background(Color.White)
    )
}