package io.github.merlin.assistant.ui.screen.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ACCOUNT_ROUTE = "account"

fun NavController.navigateToAccount() {
    navigate(ACCOUNT_ROUTE)
}

fun NavGraphBuilder.accountDestination(navController: NavController) {
    composable(ACCOUNT_ROUTE) {
        AccountScreen(navController)
    }
}