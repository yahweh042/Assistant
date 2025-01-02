package io.github.merlin.assistant.ui.screen.function.jiange

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val JIAN_GE_ROUTE = "jian_ge"

fun NavController.navigateToJianGe() {
    navigate(JIAN_GE_ROUTE)
}

fun NavGraphBuilder.jianGeDestination(navController: NavController) {
    composable(JIAN_GE_ROUTE) {
        JianGeScreen(navController)
    }
}