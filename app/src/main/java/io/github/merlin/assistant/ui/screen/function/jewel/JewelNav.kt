package io.github.merlin.assistant.ui.screen.function.jewel

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val JEWEL_ROUTE = "jewel"

fun NavController.navigateToJewel() {
    navigate(JEWEL_ROUTE)
}


fun NavGraphBuilder.jewelDestination(navController: NavController) {
    composable(JEWEL_ROUTE) {
        JewelScreen(navController)
    }
}