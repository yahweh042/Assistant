package io.github.merlin.assistant.ui.screen.function.pot.exchange

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
import io.github.merlin.assistant.data.network.response.QueryExchangeResponse
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.ViewState

@Composable
fun ExchangePage(paddingValues: PaddingValues) {

    val viewModel: ExchangeViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            is ExchangeEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewState) {
            ViewState.Loading -> LoadingContent()
            is ViewState.Error -> ErrorContent(
                msg = viewState.msg,
                retry = { viewModel.trySendAction(ExchangeAction.RetryQueryExchange) }
            )

            is ViewState.Success<List<QueryExchangeResponse.Exchange>> -> ExchangeContent(
                paddingValues = paddingValues,
                exchanges = viewState.data,
                onExchange = { viewModel.trySendAction(ExchangeAction.Exchange(it)) }
            )
        }
    }

    LoadingDialog(loadingDialogState = state.loadingDialogState)

}

@Composable
fun ExchangeContent(
    paddingValues: PaddingValues,
    exchanges: List<QueryExchangeResponse.Exchange>,
    onExchange: (Int) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(exchanges) { exchange ->
            ElevatedCard(shape = ShapeDefaults.Medium) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(text = exchange.desc) },
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = "奖励", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = exchange.award.items.joinToString(" ") { "${it.name}*${it.num}" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(text = "兑换所需", style = MaterialTheme.typography.bodyLarge)
                    if (exchange.goodsInfo == null) {
                        Text(
                            text = "没有物品",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            text = "${exchange.goodsInfo.name} 拥有 ${exchange.goodsInfo.num}",
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
                        enabled = exchange.exchanged == 0,
                        onClick = { onExchange(exchange.id) },
                    ) {
                        Text(text = "兑换")
                    }
                }
            }
        }
    }

}