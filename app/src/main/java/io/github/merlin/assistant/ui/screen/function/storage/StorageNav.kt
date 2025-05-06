package io.github.merlin.assistant.ui.screen.function.storage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val STORAGE_ROUTE = "storage"

fun NavController.navigateToStorage() {
    navigate(STORAGE_ROUTE)
}

fun NavGraphBuilder.storageDestination(navController: NavController) {
    composable(STORAGE_ROUTE) {
        StorageScreen(navController)
    }
}