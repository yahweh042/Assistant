package io.github.merlin.assistant.ui.screen.function.jiange

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                title = {
                    Row {
                        TextButton(
                            onClick = { viewModel.trySendAction(JianGeAction.Query(isSpecial = 0)) },
                            colors = if (state.isSpecial == 0) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                        ) {
                            Text(text = "普通关卡")
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        TextButton(
                            onClick = { viewModel.trySendAction(JianGeAction.Query(isSpecial = 1)) },
                            colors = if (state.isSpecial == 1) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                        ) {
                            Text(text = "秘境关卡")
                        }
                    }
                },
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
                retry = { viewModel.trySendAction(JianGeAction.Query(isSpecial = 0)) },
            )

            is ViewState.Success<*> -> JianGeContent(
                modifier = Modifier.padding(paddingValues),
                queryState = viewState.data as JianGeUiState.QueryState,
                viewModel = viewModel,
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp, 0.dp),
    ) {

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(text = "最高层数：${queryState.highestPassFloor}")
                Text(text = "结束时间：${queryState.activityEndTime}")
            }
        }
        Button(onClick = { viewModel.trySendAction(JianGeAction.Begin) }) {
            Text(text = "挑战")
        }

        LazyColumn {
            items(queryState.levelArray) { level ->
                Text(text = level.levelId.toString())
            }
        }
    }
}

