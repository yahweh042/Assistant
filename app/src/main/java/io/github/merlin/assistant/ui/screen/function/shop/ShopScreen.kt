package io.github.merlin.assistant.ui.screen.function.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.PagerTabIndicator
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController
) {

    val tabLabels = listOf("杂货铺", "兑换", "万能", "法器", "晶核", "其他")
    val viewModel: ShopViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { state.shopTypes.size }

    val selectedTabIndex by rememberUpdatedState(pagerState.currentPage)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (state.shopTypes.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = selectedTabIndex,
                                containerColor = Color.Transparent,
                                edgePadding = 0.dp,
                                divider = {},
                                indicator = { tabPositions ->
                                    PagerTabIndicator(
                                        pagerState = pagerState,
                                        tabPositions = tabPositions,
                                    )
                                }
                            ) {
                                state.shopTypes.fastForEachIndexed { index, shopType ->
                                    if (shopType.show) {
                                        Tab(
                                            modifier = Modifier.height(48.dp),
                                            selected = index == selectedTabIndex,
                                            onClick = {
                                                scope.launch {
                                                    pagerState.scrollToPage(index)
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = shopType.name,
                                                fontWeight = if (index == selectedTabIndex) FontWeight.Bold else FontWeight.Normal,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
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
                                imageVector = Icons.AutoMirrored.Rounded.List,
                                contentDescription = "",
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
        ) {
            LazyColumn(contentPadding = paddingValues) {
                items(100) { num ->
                    Text(
                        text = num.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun TopAppBarTab() {

}