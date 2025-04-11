package io.github.merlin.assistant.ui.screen.function.pot.arena

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ARENA_ROUTE = "arena"

fun NavController.navigateToArena() {
    navigate(ARENA_ROUTE)
}

fun NavGraphBuilder.arenaDestination(navController: NavController) {
    composable(ARENA_ROUTE) {
        ArenaScreen(navController)
    }
}