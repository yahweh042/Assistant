package io.github.merlin.assistant.ui.screen.account

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.screen.login.navigationToLogin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val viewModel: AccountViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEvent(viewModel) { event ->
        when (event) {
            AccountEvent.NavigateToLogin -> navController.navigationToLogin()
            is AccountEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "账号管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                actions = {
                    LoginActionMenu(
                        onCookieLogin = { viewModel.trySendAction(AccountAction.ShowCookieDialog) },
                        onWebLogin = { viewModel.trySendAction(AccountAction.AddAccountButtonClick) },
                    )
                }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(state.accounts) { item ->
                ListItem(
                    modifier = Modifier
                        .combinedClickable(
                            onLongClick = { viewModel.trySendAction(AccountAction.EditAccount(item)) },
                            onClick = {
                                viewModel.trySendAction(
                                    AccountAction.SwitchAccountClick(item.uid)
                                )
                            }
                        ),
                    headlineContent = { Text(text = item.name) },
                    supportingContent = { Text(text = item.uid) },
                    leadingContent = {
                        SubcomposeAsyncImage(
                            model = item.headImg,
                            modifier = Modifier.size(32.dp),
                            contentDescription = null,
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
                    trailingContent = {
                        Checkbox(checked = item.isActive == true, onCheckedChange = null)
                    },
                )
            }
        }

        EditAccountDialog(
            state = state.editAccountDialogState,
            onHideDialog = { viewModel.trySendAction(AccountAction.HideEditAccountDialog) },
            onChangeToken = { viewModel.trySendAction(AccountAction.ChangeToken(it)) },
            onUpdateToken = { viewModel.trySendAction(AccountAction.UpdateToken) },
        )

        LoginWithCookieDialog(
            state = state.loginWithCookieState,
            onHideDialog = { viewModel.trySendAction(AccountAction.HideLoginWithCookieDialog) },
            onUpdateCookie = { viewModel.trySendAction(AccountAction.UpdateCookie(it)) },
            onConfirmCookie = { viewModel.trySendAction(AccountAction.ConfirmCookie(it)) }
        )

    }

}

@Composable
fun LoginWithCookieDialog(
    state: AccountUiState.LoginWithCookieState,
    onHideDialog: () -> Unit,
    onUpdateCookie: (String) -> Unit,
    onConfirmCookie: (String) -> Unit,
) {
    when (state) {
        AccountUiState.LoginWithCookieState.Hide -> Unit
        is AccountUiState.LoginWithCookieState.Show -> AssistantDialog(
            onDismissRequest = { onHideDialog() },
            confirmButton = {
                TextButton(onClick = { onConfirmCookie(state.cookie) }) {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { onHideDialog() }) {
                    Text(text = "取消")
                }
            },
            title = { Text(text = "输入Cookie") },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.cookie,
                    onValueChange = { onUpdateCookie(it) },
                )
            }
        )
    }
}

@Composable
fun EditAccountDialog(
    state: AccountUiState.EditAccountDialogState,
    onHideDialog: () -> Unit,
    onChangeToken: (String) -> Unit,
    onUpdateToken: () -> Unit,
) {

    when (state) {
        AccountUiState.EditAccountDialogState.Hide -> Unit
        is AccountUiState.EditAccountDialogState.Show -> AssistantDialog(
            onDismissRequest = { onHideDialog() },
            confirmButton = {
                TextButton(onClick = { onUpdateToken() }) {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { onHideDialog() }) {
                    Text(text = "取消")
                }
            },
            title = { Text(text = "编辑账号") },
            text = {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        value = state.account.uid,
                        onValueChange = {},
                        label = { Text(text = "Uid") },
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        value = state.account.name,
                        onValueChange = {},
                        label = { Text(text = "Name") },
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        value = state.account.openid,
                        onValueChange = {},
                        label = { Text(text = "openId") },
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.account.token,
                        onValueChange = { onChangeToken(it) },
                        label = { Text(text = "token") },
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.account.cookie,
                        onValueChange = { },
                        label = { Text(text = "cookie") },
                    )
                }
            },
        )

    }

}

@Composable
fun LoginActionMenu(
    onCookieLogin: () -> Unit,
    onWebLogin: () -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    Box {

        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Account")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Cookie登录") },
                onClick = { onCookieLogin() }
            )
            DropdownMenuItem(
                text = { Text("网页登录") },
                onClick = { onWebLogin() }
            )
        }

    }


}