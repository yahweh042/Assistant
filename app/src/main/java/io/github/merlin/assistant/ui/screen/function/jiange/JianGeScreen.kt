package io.github.merlin.assistant.ui.screen.function.jiange

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LogsBottomSheet
import io.github.merlin.assistant.ui.base.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JianGeScreen(
    navController: NavController
) {

    val viewModel = hiltViewModel<JianGeViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "剑阁秘境") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (viewState) {
            ViewState.Loading -> LoadingContent(modifier = Modifier.padding(paddingValues))

            is ViewState.Error -> ErrorContent(
                modifier = Modifier.padding(paddingValues),
                msg = viewState.msg,
                retry = { viewModel.trySendAction(JianGeAction.Query) },
            )

            is ViewState.Success<*> -> JianGeContent(
                modifier = Modifier.padding(paddingValues),
                queryState = viewState.data as JianGeUiState.QueryState,
                viewModel
            )
        }
        LogsBottomSheet(
            logs = state.logs,
            showBottomSheet = state.showBottomSheet,
            paddingValues = paddingValues,
            onDismissRequest = {
                viewModel.trySendAction(JianGeAction.HideBottomSheet)
            },
        )
    }

}

@Composable
fun JianGeContent(
    modifier: Modifier = Modifier,
    queryState: JianGeUiState.QueryState,
    viewModel: JianGeViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(text = "最高层数：${queryState.highestPassFloor}")
                Text(text = "结束时间：${queryState.activityEndTime}")
            }
        }
        Button(onClick = { viewModel.trySendAction(JianGeAction.Begin) }) {
            Text(text = "begin")
        }
    }
}

