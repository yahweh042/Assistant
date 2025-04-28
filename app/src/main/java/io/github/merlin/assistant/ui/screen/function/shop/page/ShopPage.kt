package io.github.merlin.assistant.ui.screen.function.shop.page

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.GoodsIcon
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.NumberTextField
import io.github.merlin.assistant.ui.base.ShopIcon
import io.github.merlin.assistant.ui.base.ViewState

@Composable
fun ShopPage(
    shopType: String,
    contentPadding: PaddingValues,
) {

    val viewModel =
        hiltViewModel<ShopPageViewModel, ShopPageViewModelFactory>(key = "shop_page_${shopType}") {
            it.create(shopType)
        }
    val context = LocalContext.current
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState

//    LaunchedEffect(Unit) {
//        viewModel.trySendAction(ShopPageAction.RefreshShop(shopType))
//    }

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            is ShopPageEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    when (viewState) {
        ViewState.Loading -> LoadingContent()

        is ViewState.Success<ShopPageUiState.ShopPageState> -> ShopPageContent(
            state = viewState.data,
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

    CommodityInfoDialog(
        dialogState = state.dialogState,
        shopType = shopType,
        onHideDialog = { viewModel.trySendAction(ShopPageAction.HideDialog) },
        onUpdateGoodsNum = { commodityInfo, num ->
            viewModel.trySendAction(
                ShopPageAction.UpdateGoodsNum(
                    commodityInfo = commodityInfo,
                    num = num,
                )
            )
        },
        onBuyGoods = { shopType, commodityInfo, num ->
            viewModel.trySendAction(
                ShopPageAction.BuyGoods(
                    shopType = shopType,
                    commodityInfo = commodityInfo,
                    num = num,
                )
            )
        },
    )

    LoadingDialog(loadingDialogState = state.loadingDialogState)
}

@Composable
fun CommodityInfoDialog(
    dialogState: ShopPageUiState.CommodityInfoDialogState,
    shopType: String,
    onHideDialog: () -> Unit,
    onUpdateGoodsNum: (CommodityInfo, Int) -> Unit,
    onBuyGoods: (String, CommodityInfo, Int) -> Unit,
) {
    when (dialogState) {
        ShopPageUiState.CommodityInfoDialogState.Hide -> Unit
        is ShopPageUiState.CommodityInfoDialogState.Show -> AssistantDialog(
            onDismissRequest = {
                onHideDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBuyGoods(shopType, dialogState.commodityInfo, dialogState.num)
                    },
                ) {
                    Text(text = "购买")
                }
            },
            dismissButton = {
                TextButton(onClick = { onHideDialog }) {
                    Text(text = "取消")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoodsIcon(iconId = dialogState.commodityInfo.iconId)
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = dialogState.commodityInfo.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "剩余 ${dialogState.commodityInfo.remain}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        ) {
            Column {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "描述",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(text = dialogState.commodityInfo.goodsDes)
                    Text(
                        text = "效果",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(text = dialogState.commodityInfo.goodsEffect)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "数量",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NumberTextField(
                        value = dialogState.num,
                        onValueChange = {
                            onUpdateGoodsNum(dialogState.commodityInfo, it)
                        },
                        maxValue = dialogState.commodityInfo.maxNum,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "价格",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = (dialogState.commodityInfo.price * dialogState.num).toString())
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopPageContent(
    state: ShopPageUiState.ShopPageState,
    shopType: String,
    contentPadding: PaddingValues,
    showDialog: (CommodityInfo) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateRightPadding(LayoutDirection.Rtl),
                top = contentPadding.calculateTopPadding() + 40.dp,
                bottom = contentPadding.calculateBottomPadding(),
            ),
            modifier = Modifier.padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            flingBehavior = ScrollableDefaults.flingBehavior(),
        ) {
            items(state.commodityInfo, key = { it.id }) { item ->
                ShopGoodsItem(
                    shopType = shopType,
                    commodityInfo = item,
                    showDialog = showDialog,
                )
            }
        }
        ElevatedCard(
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxWidth()
                .height(30.dp),
            shape = ShapeDefaults.Medium,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShopIcon(id = shopType)
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = "${state.money}")
            }
        }
    }

}

@Composable
fun ShopGoodsBlock(commodityInfo: CommodityInfo) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(text = commodityInfo.name) },
        supportingContent = { Text(text = "剩余 ${commodityInfo.remain} 背包 ${commodityInfo.storageNum}") },
        leadingContent = { GoodsIcon(iconId = commodityInfo.iconId) }
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .padding(bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        if (commodityInfo.goodsDes.isNotEmpty()) {
            Text(text = "详情", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = commodityInfo.goodsDes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (commodityInfo.goodsEffect.isNotEmpty()) {
            Text(text = "效果", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = commodityInfo.goodsEffect,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    ElevatedCard(shape = ShapeDefaults.Medium) {
        ShopGoodsBlock(commodityInfo)
        HorizontalDivider(
            modifier = Modifier
                .height(Dp.Hairline)
                .padding(horizontal = 15.dp),
        )
        Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            FilledTonalButton(
                onClick = { showDialog(commodityInfo) },
            ) {
                ShopIcon(id = shopType)
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(text = "${commodityInfo.price}")
            }
        }
    }
}