package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.GoodsIcon
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.NumberTextField
import io.github.merlin.assistant.ui.base.ShopIcon
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
            shopType = shopType,
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
                TextButton(
                    onClick = {
                        viewModel.trySendAction(
                            ShopPageAction.BuyGoods(
                                shopType = shopType,
                                commodityInfo = dialogState.commodityInfo,
                                num = dialogState.num,
                            )
                        )
                    },
                ) {
                    Text(text = "购买")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.trySendAction(ShopPageAction.HideDialog) }) {
                    Text(text = "取消")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoodsIcon(iconId = dialogState.commodityInfo.iconId)
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        BasicText(
                            text = dialogState.commodityInfo.name,
                            style = TextStyle(fontSize = 16.sp),
                        )
                        BasicText(text = "货币：")
                    }
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "描述",
                    fontSize = 16.sp,
                    color = Color.Black,
                )
                Text(text = dialogState.commodityInfo.goodsDes)
                Text(
                    text = "使用",
                    fontSize = 16.sp,
                    color = Color.Black,
                )
                Text(text = dialogState.commodityInfo.goodsEffect)
                Text(text = "库存: ${dialogState.commodityInfo.remain}")
                Text(text = "价格: ${dialogState.commodityInfo.price}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "数量",
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NumberTextField(
                        value = dialogState.num,
                        onValueChange = {
                            viewModel.trySendAction(
                                ShopPageAction.UpdateGoodsNum(dialogState.commodityInfo, it)
                            )
                        },
                        maxValue = dialogState.commodityInfo.maxNum,
                    )
                }
            }
        }
    }

}

@Composable
fun ShopPageContent(
    state: ShopPageUiState.ContentState,
    shopType: String,
    contentPadding: PaddingValues,
    showDialog: (CommodityInfo) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = contentPadding,
        modifier = Modifier.padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {


        items(state.commodityInfo, key = { it.id }) { item ->
            ShopGoodsItem(
                shopType = shopType,
                commodityInfo = item,
                showDialog = showDialog,
            )
        }
    }
}

@Composable
fun ShopGoodsItem(
    shopType: String,
    commodityInfo: CommodityInfo,
    showDialog: (CommodityInfo) -> Unit,
) {
    OutlinedCard(shape = ShapeDefaults.ExtraSmall) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            GoodsIcon(iconId = commodityInfo.iconId)
            Text(text = commodityInfo.name, softWrap = false)
            Text(text = "剩余: ${commodityInfo.remain}")
            FilledTonalButton(
                onClick = { showDialog(commodityInfo) },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeDefaults.ExtraSmall,
            ) {
                ShopIcon(id = shopType)
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(text = "${commodityInfo.price}")
            }
        }
    }
}