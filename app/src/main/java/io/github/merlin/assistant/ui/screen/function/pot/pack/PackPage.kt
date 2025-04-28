package io.github.merlin.assistant.ui.screen.function.pot.pack

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.data.network.response.GoodsInfo
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.GoodsIcon
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.ViewState

@Composable
fun PackPage(
    paddingValues: PaddingValues
) {

    val viewModel: PackViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            is PackEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewState) {
            ViewState.Loading -> LoadingContent()
            is ViewState.Error -> ErrorContent(
                msg = viewState.msg,
                retry = { viewModel.trySendAction(PackAction.RetryViewPack) },
            )

            is ViewState.Success<List<GoodsInfo>> -> PackContent(
                goodsInfos = viewState.data,
                paddingValues = paddingValues,
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.trySendAction(PackAction.PullToRefresh) },
                onUseGoods = { viewModel.trySendAction(PackAction.UseGoods(it)) }
            )
        }
    }

    LoadingDialog(loadingDialogState = state.loadingDialogState)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackContent(
    goodsInfos: List<GoodsInfo>,
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onUseGoods: (GoodsInfo) -> Unit,
) {

    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = state,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(goodsInfos) { goodsInfo ->
                ElevatedCard(shape = ShapeDefaults.Medium) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(text = goodsInfo.name) },
                        supportingContent = { Text(text = "剩余 ${goodsInfo.num}") },
                        leadingContent = { GoodsIcon(iconId = goodsInfo.iconId) }
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (goodsInfo.goodsDesc.isNotEmpty()) {
                            Text(text = "详情", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = goodsInfo.goodsDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (goodsInfo.effectDesc.isNotEmpty()) {
                            Text(text = "效果", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = goodsInfo.effectDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .height(Dp.Hairline)
                            .padding(horizontal = 15.dp),
                    )
                    Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)) {
                        Spacer(modifier = Modifier.weight(1f))
                        FilledTonalButton(
                            enabled = goodsInfo.canUse == 1,
                            onClick = { onUseGoods(goodsInfo) },
                        ) {
                            Text(text = "使用")
                        }
                    }
                }
            }
        }
    }


}