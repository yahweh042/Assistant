package io.github.merlin.assistant.ui.screen.function.storage.page

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun StoragePage(
    type: Int,
    contentPadding: PaddingValues,
) {

    val viewModel =
        hiltViewModel<StoragePageViewModel, StoragePageViewModelFactory>(key = "storage_page_${type}") {
            it.create(type)
        }
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel) { event ->
        when (event) {
            is StoragePageEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    when (viewState) {
        ViewState.Loading -> LoadingContent()

        is ViewState.Success<StoragePageUiState.StoragePageContentState> -> StoragePageContent(
            state = viewState.data,
            contentPadding = contentPadding,
            onUseGoods = { goodsInfo, num ->
                viewModel.trySendAction(StoragePageAction.UseGoods(goodsInfo, num))
            },
            onAbandonGoods = { goodsInfo ->
                viewModel.trySendAction(StoragePageAction.AbandonGoods(goodsInfo))
            },
            onExchangeGoods = { goodsInfo ->
                viewModel.trySendAction(StoragePageAction.ExchangeGoods(goodsInfo))
            },
        )

        is ViewState.Error -> ErrorContent(
            msg = viewState.msg,
            retry = { viewModel.trySendAction(StoragePageAction.RetryView) },
        )
    }

    LoadingDialog(loadingDialogState = state.loadingDialogState)

}

@Composable
fun StoragePageContent(
    state: StoragePageUiState.StoragePageContentState,
    contentPadding: PaddingValues,
    onUseGoods: (GoodsInfo, Int) -> Unit,
    onAbandonGoods: (GoodsInfo) -> Unit,
    onExchangeGoods: (GoodsInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 15.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(state.goodsInfos) { goodsInfo ->
            ElevatedCard(shape = ShapeDefaults.Medium) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(text = goodsInfo.name) },
                    supportingContent = { Text(text = "背包 ${goodsInfo.num}") },
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
                if (goodsInfo.canOperate) {
                    HorizontalDivider(
                        modifier = Modifier
                            .height(Dp.Hairline)
                            .padding(horizontal = 15.dp),
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        if (goodsInfo.canAbandon == 1) {
                            FilledTonalButton(onClick = { onAbandonGoods(goodsInfo) }) {
                                Text(text = "丢弃")
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (goodsInfo.canExchange) {
                            FilledTonalButton(onClick = { onExchangeGoods(goodsInfo) }) {
                                Text(text = "兑换")
                            }
                        }
                        if (goodsInfo.canUse == 1 || goodsInfo.canUseBatch == 1) {
                            FilledTonalButton(
                                onClick = { onUseGoods(goodsInfo, 1) },
                            ) {
                                Text(text = "使用")
                            }
                        }
                    }
                }
            }
        }
    }
}
