package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.data.network.response.CommodityInfo
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

    LaunchedEffect(Unit) {
        viewModel.trySendAction(ShopPageAction.RefreshShop(shopType))
    }

    when (viewState) {
        ViewState.Loading -> LoadingContent()

        is ViewState.Success<*> -> {
            val contentState = viewState.data as ShopPageUiState.ContentState
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = contentPadding) {
                items(contentState.commodityInfo, key = { it.id }) { item ->
                    ShopGoodsItem(item)
                }
            }
        }

        is ViewState.Error -> ErrorContent(msg = viewState.msg) { }
    }

}

@Composable
fun ShopGoodsItem(
    commodityInfo: CommodityInfo
) {
    ListItem(
        modifier = Modifier.clickable {  },
        leadingContent = { GoodsIcon(iconId = commodityInfo.iconId) },
        headlineContent = { Text(text = commodityInfo.name) },
        supportingContent = {
            Column {
                Text(text = "描述: ${commodityInfo.goodsDes}")
                Text(text = "使用: ${commodityInfo.goodsEffect}")
                Text(text = "库存: ${commodityInfo.remain}")
            }
        }
    )
}