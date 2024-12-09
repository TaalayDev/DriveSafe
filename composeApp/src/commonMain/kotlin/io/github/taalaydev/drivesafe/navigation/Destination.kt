package io.github.taalaydev.drivesafe.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.taalaydev.drivesafe.ui.screens.ExamMode

sealed class Destination(val route: String) {
    operator fun invoke(): String = route.substringBefore("?")

    open val args: List<NamedNavArgument> = emptyList()

    data object Splash : Destination(route = Routes.SPLASH)
    data object Home : Destination(route = Routes.HOME)
    data object Rules : Destination(route = Routes.RULES)
    data object Rule : Destination(route = Routes.RULE) {
        override val args = listOf(navArgument("ruleId") { type = NavType.StringType })

        operator fun invoke(ruleId: String): String {
            return route.appendParams("ruleId" to ruleId)
        }
    }
    data object About : Destination(route = Routes.ABOUT)
    data object Signs : Destination(route = Routes.SIGNS)
    data object Tests : Destination(route = Routes.TESTS)
    data object QuickTest : Destination(route = Routes.QUICK_TEST)
    data object MiniGames : Destination(route = Routes.MINI_GAMES)
    data object SignMatch : Destination(route = Routes.SIGN_MATCH)
    data object TrafficPuzzle : Destination(route = Routes.TRAFFIC_PUZZLE)
    data object SpeedQuiz : Destination(route = Routes.SPEED_QUIZ)
    data object Exam : Destination(route = "exam/{mode}") {
        override val args = listOf(
            navArgument("mode") { type = NavType.StringType }
        )

        operator fun invoke(mode: ExamMode): String {
            return route.replace("{mode}", mode.toRoute())
        }
    }

    object Routes {
        const val SPLASH = "splash"
        const val HOME = "home"
        const val RULES = "rules"
        const val RULE = "rule/{ruleId}"
        const val SIGNS = "signs"
        const val TESTS = "tests"
        const val QUICK_TEST = "quickTest"
        const val ABOUT = "about"
        const val MINI_GAMES = "miniGames"
        const val SIGN_MATCH = "signMatch"
        const val TRAFFIC_PUZZLE = "trafficPuzzle"
        const val SPEED_QUIZ = "speed_quiz"
        const val EXAM = "exam"
    }
}

internal fun String.appendParams(vararg params: Pair<String, Any?>): String {
    var result = this
    params.forEach { (key, value) ->
        if (value == null) return@forEach
        result = result.replace("{$key}", value.toString())
    }
    return result
}