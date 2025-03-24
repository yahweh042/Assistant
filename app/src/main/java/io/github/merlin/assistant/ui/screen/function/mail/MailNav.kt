package io.github.merlin.assistant.ui.screen.function.mail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MAIL_ROUTE = "mail"

fun NavController.navigateToMail() {
    navigate(MAIL_ROUTE)
}

fun NavGraphBuilder.mailDestination(navController: NavController) {
    composable(MAIL_ROUTE) {
        MailScreen(navController)
    }
}