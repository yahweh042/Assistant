package io.github.merlin.assistant.ui.screen.function.mail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.LoadingContent
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

    Scaffold(
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { viewModel.trySendAction(MailAction.GetMails(2)) },
                            colors = if (state.type == 2) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                        ) {
                            Text(text = "系统邮件")
                        }
                        TextButton(
                            onClick = { viewModel.trySendAction(MailAction.GetMails(3)) },
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
        when (viewState) {
            ViewState.Loading -> LoadingContent()
            is ViewState.Error -> {
                ErrorContent(
                    msg = viewState.msg,
                    retry = { viewModel.trySendAction(MailAction.GetMails()) }
                )
            }

            is ViewState.Success<*> -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues
            ) {
                val mails = (viewState.data as MailUiState.MailState).mails
                items(mails) { mail ->
                    ListItem(
                        headlineContent = { Text(text = mail.title) },
                        supportingContent = { Text(text = mail.sender) },
                        trailingContent = { Text(text = mail.time) },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.MailOutline, contentDescription = "")
                        }
                    )
                }
            }
        }
    }

}