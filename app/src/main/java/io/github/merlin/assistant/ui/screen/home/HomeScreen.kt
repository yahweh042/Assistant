package io.github.merlin.assistant.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import io.github.merlin.assistant.R
import io.github.merlin.assistant.data.local.model.Account
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.screen.account.navigateToAccount
import io.github.merlin.assistant.ui.screen.function.jewel.navigateToJewel
import io.github.merlin.assistant.ui.screen.function.jiange.navigateToJianGe
import io.github.merlin.assistant.ui.screen.function.pot.navigateToPot
import io.github.merlin.assistant.ui.screen.function.shop.navigateToShop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
) {

    val viewModel: MainViewModel = hiltViewModel()

    val state by viewModel.stateFlow.collectAsState()

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            HomeEvent.NavigateToAccount -> navController.navigateToAccount()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AccountCard(
                account = state.account,
                onClick = {
                    viewModel.trySendAction(HomeAction.AccountCardClick)
                },
            )
            LazyColumn {
                item {
                    ListItem(
                        modifier = Modifier.clickable { navController.navigateToJewel() },
                        headlineContent = { Text(text = "矿山争夺") },
                        trailingContent = {
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        },
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.clickable { navController.navigateToPot() },
                        headlineContent = { Text(text = "壶中天地") },
                        trailingContent = {
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        },
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.clickable { navController.navigateToJianGe() },
                        headlineContent = { Text(text = "剑阁秘境") },
                        trailingContent = {
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        },
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.clickable { navController.navigateToShop() },
                        headlineContent = { Text(text = "商店") },
                        trailingContent = {
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun AccountCard(
    account: Account?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp),
        onClick = onClick,
    ) {
        ListItem(
            headlineContent = { Text(text = account?.name ?: "未登录") },
            supportingContent = { Text(text = account?.uid ?: "点击登录") },
            leadingContent = {
                SubcomposeAsyncImage(
                    model = account?.headImg,
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
        )
    }
}