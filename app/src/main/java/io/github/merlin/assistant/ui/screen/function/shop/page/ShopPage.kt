package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.GoodsIcon
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.ViewState

@Composable
fun ShopPage(
    shopType: String,
    contentPadding: PaddingValues,
) {

    val viewModel: ShopPageViewModel = hiltViewModel(key = "shop_page_${shopType}")
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val dialogState = state.dialogState

    LaunchedEffect(Unit) {
        viewModel.trySendAction(ShopPageAction.RefreshShop(shopType))
    }

    when (viewState) {
        ViewState.Loading -> LoadingContent()

        is ViewState.Success<*> -> ShopPageContent(
            state = viewState.data as ShopPageUiState.ContentState,
            contentPadding = contentPadding,
            showDialog = {
                viewModel.trySendAction(ShopPageAction.ShowDialog(it))
            }
        )

        is ViewState.Error -> ErrorContent(
            msg = viewState.msg,
            retry = { viewModel.trySendAction(ShopPageAction.RefreshShop(shopType)) },
        )
    }

    when (dialogState) {
        ShopPageUiState.CommodityInfoDialogState.Hide -> Unit
        is ShopPageUiState.CommodityInfoDialogState.Show -> AssistantDialog(
            onDismissRequest = {
                viewModel.trySendAction(ShopPageAction.HideDialog)
            },
            confirmButton = {
                TextButton(onClick = {}) {
                    Text(text = "购买")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.trySendAction(ShopPageAction.HideDialog) }) {
                    Text(text = "取消")
                }
            },
            title = {

            }
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoodsIcon(iconId = dialogState.commodityInfo.iconId)
                    Text(text = dialogState.commodityInfo.name, modifier = Modifier.weight(1f))
                    Text(text = "货币：")
                }
                Text(text = "描述: ${dialogState.commodityInfo.goodsDes}")
                Text(text = "使用: ${dialogState.commodityInfo.goodsEffect}")
                Text(text = "库存: ${dialogState.commodityInfo.remain}")
                Text(text = "价格: ${dialogState.commodityInfo.price}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "数量：")
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = dialogState.sliderPosition,
                        onValueChange = {
                            viewModel.trySendAction(
                                ShopPageAction.UpdateSlidePosition(
                                    dialogState.commodityInfo,
                                    it
                                )
                            )
                        },
                        valueRange = 0f.rangeTo(dialogState.commodityInfo.rangeMaxNum.toFloat()),
                        steps = dialogState.commodityInfo.rangeMaxNum - 1,
                    )
                    Text(text = "${dialogState.sliderPosition.toInt()}")
                }
            }
        }
    }

}

@Composable
fun ShopPageContent(
    state: ShopPageUiState.ContentState,
    contentPadding: PaddingValues,
    showDialog: ((CommodityInfo) -> Unit)?,
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = contentPadding) {
        items(state.commodityInfo, key = { it.id }) { item ->
            ShopGoodsItem(
                commodityInfo = item,
                showDialog = showDialog,
            )
        }
    }
}

@Composable
fun ShopGoodsItem(
    commodityInfo: CommodityInfo,
    showDialog: ((CommodityInfo) -> Unit)? = null,
) {
    ListItem(
        modifier = Modifier.clickable { showDialog?.invoke(commodityInfo) },
        leadingContent = { GoodsIcon(iconId = commodityInfo.iconId) },
        headlineContent = { Text(text = commodityInfo.name) },
        supportingContent = {
            Column {
                Text(text = "描述: ${commodityInfo.goodsDes}")
                Text(text = "使用: ${commodityInfo.goodsEffect}")
                Text(text = "库存: ${commodityInfo.remain}")
                Text(text = "价格: ${commodityInfo.price}")
            }
        }
    )
}