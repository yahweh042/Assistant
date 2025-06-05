package io.github.merlin.assistant.ui.screen.function.pot.arena

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.HeadImg
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.ViewState
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaPage(
    paddingValues: PaddingValues,
    showSnackbar: (String) -> Unit,
) {

    val viewModel: ArenaViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel) { event ->
        when (event) {
            is ArenaEvent.ShowToast -> showSnackbar(event.msg)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when (viewState) {
            ViewState.Loading -> LoadingContent()

            is ViewState.Success<ArenaUiState.ArenaState> -> ArenaContent(
                state = viewState.data,
                paddingValues = paddingValues,
                onFightArena = {
                    viewModel.trySendAction(ArenaAction.FightArena(it))
                }
            )

            is ViewState.Error -> ErrorContent(
                msg = viewState.msg,
                retry = {
                    viewModel.trySendAction(ArenaAction.RetryQueryArena)
                },
            )
        }
    }
    LoadingDialog(loadingDialogState = state.loadingDialogState)
}

@Composable
fun ArenaContent(
    state: ArenaUiState.ArenaState,
    paddingValues: PaddingValues,
    onFightArena: (Int) -> Unit,
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(paddingValues),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            shape = ShapeDefaults.Medium,
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(text = "战力：${state.totalPoint}")
                Text(text = "排行：${state.selfRank}")
                Text(text = "挑战次数：${state.freeTimes}")
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        state.oppInfo.forEach { item ->
            val name = URLDecoder.decode(item.name, "utf-8")
            ListItem(
                modifier = Modifier.clickable { onFightArena(item.rank) },
                leadingContent = { HeadImg(headImgUrl = item.headImgUrl) },
                headlineContent = { Text(text = "$name(${item.level}级)") },
                supportingContent = {
                    Column {
                        Text(text = "排名：${item.rank}")
                        Text(text = "战力：${item.attackPower}")
                    }
                }
            )
        }
    }

}