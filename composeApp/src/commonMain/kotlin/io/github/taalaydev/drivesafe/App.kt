package io.github.taalaydev.drivesafe

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import io.github.taalaydev.drivesafe.data.DownloadManager
import org.jetbrains.compose.ui.tooling.preview.Preview

import io.github.taalaydev.drivesafe.navigation.MainNavHost
import io.github.taalaydev.drivesafe.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    DisposableEffect(Unit) {
        onDispose {
            DownloadManager.dispose()
        }
    }

    AppTheme {
        val navController = rememberNavController()

        MainNavHost(navController = navController)
    }
}