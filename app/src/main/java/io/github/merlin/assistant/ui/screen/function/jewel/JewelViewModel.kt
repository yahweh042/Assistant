package io.github.merlin.assistant.ui.screen.function.jewel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.local.model.JewelSettings
import io.github.merlin.assistant.repo.JewelRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JewelViewModel @Inject constructor(
    private val jewelRepo: JewelRepo
) : AbstractViewModel<JewelUiState, JewelEvent, JewelAction>(
    initialState = run {
        JewelUiState(
            logs = listOf(),
            jewelSettings = JewelSettings(),
            settingsSheetState = JewelUiState.SettingsSheetState.HideSheet
        )
    }
) {

    init {
        jewelRepo.jewelSettingsStateFlow
            .onEach { receiveJewelSettingsUpdate(it) }
            .launchIn(viewModelScope)
    }

    private var job: Job? = null

    override fun handleAction(action: JewelAction) {
        when (action) {
            JewelAction.ShowSheet -> handleShowSheet()
            JewelAction.HideSheet -> handleHideSheet()
            JewelAction.RunTask -> handleRunTask()
            is JewelAction.SwitchBuyVit -> handleSwitchBuyVit(action)
            is JewelAction.ChooseType -> handleChooseType(action)
            is JewelAction.InputChooseSecretReel -> handleInputChooseSecretReel(action)
            is JewelAction.InputFacName -> handleInputFacName(action)
            is JewelAction.InputRoleName -> handleInputRoleName(action)
            JewelAction.ResetForm -> handleJewelSettingsReset()
            JewelAction.SubmitForm -> handleJewelSettingsSubmit()
        }
    }

    private fun handleInputRoleName(action: JewelAction.InputRoleName) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jewelSettings = it.jewelSettings.copy(roleName = action.roleName))
            }
        }
    }

    private fun handleInputFacName(action: JewelAction.InputFacName) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jewelSettings = it.jewelSettings.copy(factionName = action.facName))
            }
        }
    }

    private fun handleInputChooseSecretReel(action: JewelAction.InputChooseSecretReel) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jewelSettings = it.jewelSettings.copy(chooseSecretReel = action.chooseSecretReel))
            }
        }
    }

    private fun handleSwitchBuyVit(action: JewelAction.SwitchBuyVit) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jewelSettings = it.jewelSettings.copy(buyVit = action.buyVit))
            }
        }
    }

    private fun handleChooseType(action: JewelAction.ChooseType) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jewelSettings = it.jewelSettings.copy(type = action.type),
                )
            }
        }
    }

    private fun handleJewelSettingsSubmit() {
        viewModelScope.launch {
            jewelRepo.setJewelSettings(state.jewelSettings)
            mutableStateFlow.update {
                it.copy(settingsSheetState = JewelUiState.SettingsSheetState.HideSheet)
            }
        }
    }

    private fun handleJewelSettingsReset() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jewelSettings = jewelRepo.jewelSettingsState,
                    settingsSheetState = JewelUiState.SettingsSheetState.HideSheet,
                )
            }
        }
    }

    private fun handleRunTask() {
        job?.cancel()
        mutableStateFlow.update {
            it.copy(logs = listOf("开始任务"))
        }
        job = jewelRepo.runTask().onEach { log ->
            mutableStateFlow.update {
                it.copy(logs = it.logs.plus(log))
            }
        }.launchIn(viewModelScope)
    }

    private fun handleHideSheet() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jewelSettings = jewelRepo.jewelSettingsState,
                    settingsSheetState = JewelUiState.SettingsSheetState.HideSheet
                )
            }
        }
    }

    private fun handleShowSheet() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(settingsSheetState = JewelUiState.SettingsSheetState.ShowSheet)
            }
        }
    }

    private fun receiveJewelSettingsUpdate(jewelSettings: JewelSettings) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jewelSettings = jewelSettings)
            }
        }
    }

}