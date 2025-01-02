package io.github.merlin.assistant.ui.screen.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME_ROUTE = "home"

fun NavController.navigateToHome() {
    navigate(HOME_ROUTE)
}


fun NavGraphBuilder.homeDestination(navController: NavController) {
    composable(HOME_ROUTE) {
        HomeScreen(navController)
    }
}