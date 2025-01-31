package io.github.merlin.assistant.ui.screen.function.jewel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.LogsLazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JewelScreen(
    navController: NavController
) {

    val viewModel: JewelViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "矿山争夺") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.trySendAction(JewelAction.ShowSheet) }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.trySendAction(JewelAction.RunTask) }) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "")
            }
        },
    ) { innerPadding ->
        LogsLazyColumn(modifier = Modifier.padding(innerPadding), logs = state.logs)
        if (state.settingsSheetState == JewelUiState.SettingsSheetState.ShowSheet) {
            AssistantDialog(
                onDismissRequest = { viewModel.trySendAction(JewelAction.HideSheet) },
                confirmButton = {
                    TextButton(onClick = { viewModel.trySendAction(JewelAction.SubmitForm) }) {
                        Text(text = "确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.trySendAction(JewelAction.HideSheet) }) {
                        Text(text = "取消")
                    }
                },
                title = { Text(text = "配置") },
                text = {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ) {
                            OutlinedTextField(
                                label = { Text(text = "查找方式") },
                                value = if (state.jewelSettings.type == 1) "帮派名" else if (state.jewelSettings.type == 2) "用户名" else "帮派名+用户名",
                                readOnly = true,
                                onValueChange = {},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "帮派名") },
                                    onClick = {
                                        viewModel.trySendAction(JewelAction.ChooseType(1))
                                        expanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "用户名") },
                                    onClick = {
                                        viewModel.trySendAction(JewelAction.ChooseType(2))
                                        expanded = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "帮派名+用户名") },
                                    onClick = {
                                        viewModel.trySendAction(JewelAction.ChooseType(3))
                                        expanded = false
                                    },
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "查找用户名") },
                            value = state.jewelSettings.roleName,
                            onValueChange = {
                                viewModel.trySendAction(JewelAction.InputRoleName(it))
                            },
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "查找帮派名") },
                            value = state.jewelSettings.factionName,
                            onValueChange = {
                                viewModel.trySendAction(JewelAction.InputFacName(it))
                            },
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        OutlinedTextField(
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "密卷选择") },
                            value = state.jewelSettings.chooseSecretReel,
                            onValueChange = {
                                viewModel.trySendAction(JewelAction.InputChooseSecretReel(it))
                            },
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        ListItem(
                            headlineContent = { Text(text = "是否购买体力") },
                            trailingContent = {
                                Switch(
                                    checked = state.jewelSettings.buyVit,
                                    onCheckedChange = {
                                        viewModel.trySendAction(JewelAction.SwitchBuyVit(it))
                                    }
                                )
                            },
                        )
                    }
                }
            )
        }
    }
}