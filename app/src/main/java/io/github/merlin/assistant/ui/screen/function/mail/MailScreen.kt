package io.github.merlin.assistant.ui.screen.function.mail

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.GoodsIcon
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingContent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailScreen(
    navController: NavController
) {

    val viewModel: MailViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsState()
    val viewState = state.viewState

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val context = LocalContext.current

    LaunchedEvent(viewModel) { event ->
        when(event) {
            is MailEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor.copy(
                        alpha = 0.95f
                    )
                ),
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { viewModel.trySendAction(MailAction.QueryMails(2)) },
                            colors = if (state.type == 2) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                        ) {
                            Text(text = "系统邮件")
                        }
                        TextButton(
                            onClick = { viewModel.trySendAction(MailAction.QueryMails(3)) },
                            colors = if (state.type == 3) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                        ) {
                            Text(text = "好友邮件")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (viewState) {
                ViewState.Loading -> LoadingContent()
                is ViewState.Error -> {
                    ErrorContent(
                        msg = viewState.msg,
                        retry = { viewModel.trySendAction(MailAction.QueryMails()) }
                    )
                }

                is ViewState.Success<MailUiState.MailState> -> MailContent(
                    state = viewState.data,
                    nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                    paddingValues = paddingValues,
                    onOpenMail = { viewModel.trySendAction(MailAction.OpenMail(it)) }
                )
            }
            MailSheet(
                sheetState = state.sheetState,
                paddingValues = paddingValues,
                onHideSheet = { viewModel.trySendAction(MailAction.HideSheet) },
                onGetReward = { viewModel.trySendAction(MailAction.GetReward(it)) }
            )
            LoadingDialog(loadingDialogState = state.loadingDialogState)
        }
    }

}

@Composable
fun MailContent(
    state: MailUiState.MailState,
    nestedScrollConnection: NestedScrollConnection,
    paddingValues: PaddingValues,
    onOpenMail: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        contentPadding = paddingValues,
    ) {
        items(state.mails) { mail ->
            ListItem(
                headlineContent = { Text(text = mail.title) },
                supportingContent = { Text(text = mail.sender) },
                trailingContent = { Text(text = mail.formatTime) },
                leadingContent = {
                    Icon(
                        imageVector = if (mail.reward == 2) Icons.Filled.MarkEmailRead else Icons.Filled.MarkEmailUnread,
                        contentDescription = "",
                    )
                },
                modifier = Modifier.clickable {
                    onOpenMail(mail.id.toInt())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailSheet(
    sheetState: MailUiState.SheetState,
    paddingValues: PaddingValues,
    onHideSheet: () -> Unit,
    onGetReward: (Int) -> Unit,
) {
    val configuration = LocalConfiguration.current
    when (sheetState) {
        MailUiState.SheetState.HideSheet -> Unit
        is MailUiState.SheetState.ShowSheet -> ModalBottomSheet(
            onDismissRequest = onHideSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = { BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Horizontal) },
            shape = ShapeDefaults.Medium,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .height(configuration.screenHeightDp.dp / 2)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                Text(text = sheetState.data.title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = sheetState.data.sender,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    HorizontalDivider(modifier = Modifier.height(Dp.Hairline))
                }
                Text(text = sheetState.data.content)
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    sheetState.data.rewardList.forEach { item ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            GoodsIcon(iconId = item.iconId ?: 0L, id = item.id)
                            Text(text = "*${item.num}")
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onGetReward(sheetState.data.id.toInt()) },
                        enabled = sheetState.data.reward == 1
                    ) {
                        Text(text = if (sheetState.data.reward == 1) "领取奖励" else "已领取")
                    }
                    Button(onClick = {}) {
                        Text(text = "删除")
                    }
                }

            }
        }
    }
}