package io.github.merlin.assistant.ui.screen.function.pot.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.base.AssistantDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotSettingsScreen(
    navController: NavController
) {

    val viewModel: PotSettingsViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "壶中天地配置")
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
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(modifier = Modifier.padding(paddingValues)) {
                ListItem(
                    modifier = Modifier.clickable { viewModel.trySendAction(PotSettingsAction.AttrFilterChange(!state.attrFilter)) },
                    headlineContent = { Text(text = "词条筛选") },
                    supportingContent = { Text(text = "碰到不在配置的词条中的装备自动分解, 多个逗号分隔") },
                    trailingContent = {
                        Switch(
                            checked = state.attrFilter,
                            onCheckedChange = {
                                viewModel.trySendAction(PotSettingsAction.AttrFilterChange(it))
                            },
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.clickable { viewModel.trySendAction(PotSettingsAction.ShowEditDialog(state.attrs)) },
                    headlineContent = { Text(text = "副词条") },
                    supportingContent = { Text(text = state.attrs.ifBlank { "未配置" }) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = ""
                        )
                    },
                )
            }
        }
        EditDialog(
            editDialogState = state.editDialogState,
            viewModel = viewModel,
        )
    }

}

@Composable
fun EditDialog(
    editDialogState: PotSettingsUiState.EditDialogState,
    viewModel: PotSettingsViewModel,
) {

    when (editDialogState) {
        PotSettingsUiState.EditDialogState.Hide -> Unit
        is PotSettingsUiState.EditDialogState.Show -> AssistantDialog(
            onDismissRequest = { viewModel.trySendAction(PotSettingsAction.HideEditDialog) },
            dismissButton = {
                TextButton(onClick = { viewModel.trySendAction(PotSettingsAction.HideEditDialog) }) {
                    Text(text = "取消")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.trySendAction(
                        PotSettingsAction.AttrsChange(editDialogState.inputValue)
                    )
                }) {
                    Text(text = "确定")
                }
            },
            title = { Text(text = "编辑副词条") },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    value = editDialogState.inputValue,
                    onValueChange = {
                        viewModel.trySendAction(
                            PotSettingsAction.ShowEditDialog(it)
                        )
                    }
                )
            },
        )
    }
}