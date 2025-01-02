package io.github.merlin.assistant.ui.screen.function.pot

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val POT_ROUTE = "pot"

fun NavController.navigateToPot() {
    navigate(POT_ROUTE)
}

fun NavGraphBuilder.potDestination(navController: NavController) {
    composable(POT_ROUTE) {
        PotScreen(navController)
    }
}