package io.github.merlin.assistant.ui.screen.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val LOGIN_ROUTE = "login"

fun NavController.navigationToLogin() {
    navigate(LOGIN_ROUTE)
}

fun NavGraphBuilder.loginDestination(navController: NavController) {
    composable(
        route = LOGIN_ROUTE
    ) {
        LoginScreen(navController)
    }
}