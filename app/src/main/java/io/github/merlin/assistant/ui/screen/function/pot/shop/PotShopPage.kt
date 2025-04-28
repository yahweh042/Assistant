package io.github.merlin.assistant.ui.screen.function.pot.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.merlin.assistant.ui.screen.function.shop.page.ShopPage
import kotlinx.coroutines.launch

@Composable
fun PotShopPage(
    paddingValues: PaddingValues
) {

    val options = remember { mutableStateListOf("每日", "每月") }
    val pagerState = rememberPagerState { options.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(paddingValues),
    ) {

        SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth()) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    onClick = { scope.launch { pagerState.scrollToPage(index) } },
                    selected = pagerState.currentPage == index,
                ) {
                    Text(text = label)
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalPager(state = pagerState, userScrollEnabled = false) { index ->
            when (index) {
                0 -> ShopPage(
                    shopType = "53",
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                )

                1 -> ShopPage(
                    shopType = "54",
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
                )
            }
        }

    }

}