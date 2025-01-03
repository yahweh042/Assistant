package io.github.merlin.assistant.ui.screen.function.shop

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SHOP_ROUTE = "shop"

fun NavController.navigateToShop() {
    navigate(SHOP_ROUTE)
}

fun NavGraphBuilder.shopDestination(navController: NavController) {
    composable(SHOP_ROUTE) {
        ShopScreen(navController)
    }
}