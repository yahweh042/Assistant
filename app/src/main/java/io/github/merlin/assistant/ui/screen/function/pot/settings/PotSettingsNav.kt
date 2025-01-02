package io.github.merlin.assistant.ui.screen.function.pot.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val POT_SETTINGS_ROUTE = "pot_settings"

fun NavController.navigateToPotSettings() {
    navigate(POT_SETTINGS_ROUTE)
}

fun NavGraphBuilder.potSettingsDestination(navController: NavController) {
    composable(POT_SETTINGS_ROUTE) {
        PotSettingsScreen(navController)
    }
}