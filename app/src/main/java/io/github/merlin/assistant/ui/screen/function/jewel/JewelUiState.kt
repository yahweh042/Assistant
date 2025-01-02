package io.github.merlin.assistant.ui.screen.function.jewel

import io.github.merlin.assistant.data.local.model.JewelSettings

data class JewelUiState(
    val logs: List<String>,
    val jewelSettings: JewelSettings,
    val settingsSheetState: SettingsSheetState
) {

    sealed class SettingsSheetState {
        data object ShowSheet : SettingsSheetState()
        data object HideSheet : SettingsSheetState()
    }

}

