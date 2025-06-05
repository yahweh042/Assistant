package io.github.merlin.assistant.ui.screen.function.pot

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.TabTextButton
import io.github.merlin.assistant.ui.screen.function.pot.arena.ArenaPage
import io.github.merlin.assistant.ui.screen.function.pot.exchange.ExchangePage
import io.github.merlin.assistant.ui.screen.function.pot.home.PotHomePage
import io.github.merlin.assistant.ui.screen.function.pot.pack.PackPage
import io.github.merlin.assistant.ui.screen.function.pot.settings.navigateToPotSettings
import io.github.merlin.assistant.ui.screen.function.pot.shop.PotShopPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotScreen(
    navController: NavController,
) {

    val pageNames = remember { mutableStateListOf("壶中天地", "竞技场", "背包", "商店", "兑换") }
    val pagerState = rememberPagerState { pageNames.size }
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                title = {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        pageNames.forEachIndexed { index, pageName ->
                            item {
                                TabTextButton(
                                    text = pageName,
                                    active = pagerState.currentPage == index,
                                    onClick = { scope.launch { pagerState.scrollToPage(index) } }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigateToPotSettings() }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = hostState)
        },
    ) { paddingValues ->
        HorizontalPager(state = pagerState, userScrollEnabled = false) { index ->
            when (index) {
                0 -> PotHomePage(paddingValues = paddingValues)
                1 -> ArenaPage(
                    paddingValues = paddingValues,
                    showSnackbar = {
                        scope.launch {
                            hostState.showSnackbar(message = it, withDismissAction = true)
                        }
                    },
                )

                2 -> PackPage(paddingValues = paddingValues)
                3 -> PotShopPage(paddingValues = paddingValues)
                4 -> ExchangePage(paddingValues = paddingValues)
            }
        }
    }

}