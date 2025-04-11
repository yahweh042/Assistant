package io.github.merlin.assistant.ui.screen.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.merlin.assistant.ui.screen.account.accountDestination
import io.github.merlin.assistant.ui.screen.function.jewel.jewelDestination
import io.github.merlin.assistant.ui.screen.function.jiange.jianGeDestination
import io.github.merlin.assistant.ui.screen.function.mail.mailDestination
import io.github.merlin.assistant.ui.screen.function.pot.arena.arenaDestination
import io.github.merlin.assistant.ui.screen.function.pot.potDestination
import io.github.merlin.assistant.ui.screen.function.pot.settings.potSettingsDestination
import io.github.merlin.assistant.ui.screen.function.shop.shopDestination
import io.github.merlin.assistant.ui.screen.home.HOME_ROUTE
import io.github.merlin.assistant.ui.screen.home.homeDestination
import io.github.merlin.assistant.ui.screen.login.loginDestination

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = navController,
        startDestination = HOME_ROUTE,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { fullWidth: Int -> fullWidth })
        },
        exitTransition = {
            fadeOut()
        },
        popEnterTransition = {
            fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { fullWidth: Int -> fullWidth })
        }
    ) {
        homeDestination(navController)
        accountDestination(navController)
        loginDestination(navController)
        jewelDestination(navController)
        potDestination(navController)
        potSettingsDestination(navController)
        jianGeDestination(navController)
        shopDestination(navController)
        mailDestination(navController)
        arenaDestination(navController)
    }

}