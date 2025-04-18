package io.github.merlin.assistant.ui.screen.function.pot.arena

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.ViewState
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    navController: NavController,
) {

    val viewModel: ArenaViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel) { event ->
        when (event) {
            is ArenaEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "竞技场") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (viewState) {
                ViewState.Loading -> LoadingContent()
                is ViewState.Error -> ErrorContent(
                    msg = viewState.msg,
                    retry = {
                        viewModel.trySendAction(ArenaAction.RetryQueryArena)
                    },
                )

                is ViewState.Success<*> -> ArenaContent(
                    state = viewState.data as ArenaUiState.ArenaState,
                    paddingValues = paddingValues,
                    onFightArena = {
                        viewModel.trySendAction(ArenaAction.FightArena(it))
                    }
                )
            }
        }
        LoadingDialog(loadingDialogState = state.loadingDialogState)
    }
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
            val name = URLDecoder.decode((item.name), "utf-8")
            ListItem(
                modifier = Modifier.clickable { onFightArena(item.rank) },
                leadingContent = {
                    SubcomposeAsyncImage(
                        model = item.headImgUrl,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        error = {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = null,
                            )
                        },
                        loading = {
                            CircularProgressIndicator()
                        }
                    )
                },
                headlineContent = { Text(text = "$name(${item.level}级)") },
                supportingContent = {
                    Column {
                        Text(text = "战力：${item.attackPower}")
                        Text(text = "排名：${item.rank}")
                    }
                }
            )
        }
    }

}