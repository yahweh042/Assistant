package io.github.merlin.assistant.ui.screen.account

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.screen.login.navigationToLogin

@OptIn(ExperimentalMaterial3Api::class)
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
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "添加账号") },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "") },
                onClick = {
                    viewModel.trySendAction(AccountAction.AddAccountButtonClick)
                }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(state.accounts) { item ->
                ListItem(
                    modifier = Modifier
                        .clickable {
                            viewModel.trySendAction(AccountAction.SwitchAccountClick(item.uid))
                        },
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
                        Checkbox(checked = item.isActive ?: false, onCheckedChange = null)
                    },
                )
            }
        }
    }

}