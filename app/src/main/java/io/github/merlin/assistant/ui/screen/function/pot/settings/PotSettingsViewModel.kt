package io.github.merlin.assistant.ui.screen.function.pot.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.local.model.PotSettings
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PotSettingsViewModel @Inject constructor(
    val potRepo: PotRepo,
) : AbstractViewModel<PotSettingsUiState, PotSettingsEvent, PotSettingsAction>(
    initialState = run {
        PotSettingsUiState()
    }
) {

    init {
        potRepo.potSettingsStateFlow
            .onEach { receivePotSettingsUpdate(it) }
            .launchIn(viewModelScope)
    }


    override fun handleAction(action: PotSettingsAction) {
        when (action) {
            is PotSettingsAction.AttrFilterChange -> handleAttrFilterChange(action.attrFilter)
            is PotSettingsAction.AttrsChange -> handleAttrsChange(action.attrs)
            is PotSettingsAction.ShowEditDialog -> handleShowEditDialog(action.inputValue)
            PotSettingsAction.HideEditDialog -> handleHideEditDialog()
        }
    }

    private fun handleHideEditDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(editDialogState = PotSettingsUiState.EditDialogState.Hide)
            }
        }
    }

    private fun handleShowEditDialog(inputValue: String) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(editDialogState = PotSettingsUiState.EditDialogState.Show(inputValue = inputValue))
            }
        }
    }

    private fun handleAttrFilterChange(attrFilter: Boolean) {
        viewModelScope.launch {
            potRepo.setPotSettings(
                potSettings = PotSettings(
                    attrs = state.attrs,
                    attrFilter = attrFilter,
                )
            )
        }
    }

    private fun handleAttrsChange(attrs: String) {
        viewModelScope.launch {
            potRepo.setPotSettings(
                potSettings = PotSettings(
                    attrs = attrs,
                    attrFilter = state.attrFilter,
                )
            )
            mutableStateFlow.update {
                it.copy(editDialogState = PotSettingsUiState.EditDialogState.Hide)
            }
        }
    }

    private fun receivePotSettingsUpdate(potSettings: PotSettings) {
        mutableStateFlow.update {
            it.copy(attrFilter = potSettings.attrFilter, attrs = potSettings.attrs)
        }
    }

}
