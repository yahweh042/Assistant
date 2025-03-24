package io.github.merlin.assistant.ui.screen.function.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.PagerTabIndicator
import io.github.merlin.assistant.ui.screen.function.shop.page.ShopPage
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController
) {

    val viewModel: ShopViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { state.shopTypes.size }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    LaunchedEvent(viewModel) { event ->
        when (event) {
            is ShopEvent.ScrollToPage -> pagerState.scrollToPage(event.page)
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        listState.animateScrollToItem(pagerState.currentPage)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (state.shopTypes.isNotEmpty()) {
                            LazyRow(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, state = listState) {
                                state.shopTypes.fastForEachIndexed { index, shopType ->
                                    if (shopType.show) {
                                        item {
                                            TextButton(
                                                onClick = {
                                                    scope.launch { pagerState.scrollToPage(index) }
                                                },
                                                colors = if (index == pagerState.currentPage) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                                            ) {
                                                Text(text = shopType.name)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor.copy(
                            alpha = 0.95f
                        )
                    ),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "",
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(state = pagerState) { page ->
            ShopPage(
                shopType = state.shopTypes[page].type,
                contentPadding = paddingValues,
            )
        }
    }
}

@Composable
fun TopAppBarTab() {

}