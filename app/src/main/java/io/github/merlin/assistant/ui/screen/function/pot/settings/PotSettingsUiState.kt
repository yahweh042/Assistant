package io.github.merlin.assistant.ui.screen.function.pot.settings

data class PotSettingsUiState(
    val attrFilter: Boolean = false,
    val attrs: String = "",
    val editDialogState: EditDialogState = EditDialogState.Hide,
) {

    sealed class EditDialogState {

        data class Show(val inputValue: String) : EditDialogState()
        data object Hide : EditDialogState()

    }

}