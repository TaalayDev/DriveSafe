package io.github.taalaydev.drivesafe.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.taalaydev.drivesafe.ui.screens.ExamMode
import io.github.taalaydev.drivesafe.ui.screens.HomeScreen
import io.github.taalaydev.drivesafe.ui.screens.MiniGamesScreen
import io.github.taalaydev.drivesafe.ui.screens.MockExamScreen
import io.github.taalaydev.drivesafe.ui.screens.PracticeTestsScreen
import io.github.taalaydev.drivesafe.ui.screens.QuickTestScreen
import io.github.taalaydev.drivesafe.ui.screens.RoadSignsScreen
import io.github.taalaydev.drivesafe.ui.screens.RuleDetailScreen
import io.github.taalaydev.drivesafe.ui.screens.RulesScreen
import io.github.taalaydev.drivesafe.ui.screens.SplashScreen
import io.github.taalaydev.drivesafe.ui.screens.games.SignMatchGame
import io.github.taalaydev.drivesafe.ui.screens.games.SpeedQuizGame
import io.github.taalaydev.drivesafe.ui.screens.games.TrafficPuzzleGame

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Splash.route
    ) {
        composable(Destination.Splash.route) {
            SplashScreen { navController.navigate(Destination.Home()) }
        }
        composable(Destination.Home.route) {
            HomeScreen(
                onNavigateToRules = { navController.navigate(Destination.Rules()) },
                onNavigateToSigns = { navController.navigate(Destination.Signs()) },
                onNavigateToTests = { navController.navigate(Destination.Tests()) },
                onNavigateToGames = { navController.navigate(Destination.MiniGames()) }
            )
        }
        composable(Destination.Rules.route) {
            RulesScreen(onBackPressed = {
                navController.popBackStack()
            }, onLessonSelected = { id ->
                navController.navigate(Destination.Rule(id))
            })
        }
        composable(
            Destination.Rule.route,
            arguments = Destination.Rule.args
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId") ?: return@composable

            RuleDetailScreen(
                lessonId = ruleId,
                onBackPressed = {
                    navController.popBackStack()
                },
                onShareClick = {}
            )
        }
        composable(Destination.Signs.route) {
            RoadSignsScreen(onBackPressed = {
                navController.popBackStack()
            }, onSignSelected = {})
        }
        composable(Destination.Tests.route) {
            PracticeTestsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onStartTest = {
                    if (it == "quick") {
                        navController.navigate(Destination.QuickTest())
                    } else if (it == "mock") {
                        navController.navigate(Destination.Exam(ExamMode.Mock))
                    }
                }
            )
        }
        composable(Destination.QuickTest.route) {
            QuickTestScreen(
                onBackPressed = { navController.popBackStack() },
                onFinishTest = { /* Handle test completion */ }
            )
        }
        composable(Destination.MiniGames.route) {
            MiniGamesScreen(
                onBackPressed = { navController.popBackStack() },
                onGameSelected = {
                    if (it == "sign_match") {
                        navController.navigate(Destination.SignMatch())
                    } else if (it == "traffic_puzzle") {
                        navController.navigate(Destination.TrafficPuzzle())
                    } else if (it == "speed_quiz") {
                        navController.navigate(Destination.SpeedQuiz())
                    }
                }
            )
        }
        composable(Destination.SignMatch.route) {
            SignMatchGame(
                onBackPressed = { navController.popBackStack() },
                onGameComplete = { /* Handle game completion */ }
            )
        }
        composable(Destination.TrafficPuzzle.route) {
            TrafficPuzzleGame(
                onBackPressed = { navController.popBackStack() },
                onGameComplete = { /* Handle game completion */ }
            )
        }
        composable(Destination.SpeedQuiz.route) {
            SpeedQuizGame(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(
            route = Destination.Exam.route,
            arguments = Destination.Exam.args
        ) { backStackEntry ->
            val mode = ExamMode.fromRoute(
                backStackEntry.arguments?.getString("mode")
            )
            MockExamScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}