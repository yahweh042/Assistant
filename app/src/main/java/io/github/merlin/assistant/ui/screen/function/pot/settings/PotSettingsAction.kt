package io.github.merlin.assistant.ui.screen.function.pot.settings

sealed class PotSettingsAction {

    data class AttrFilterChange(val attrFilter: Boolean) : PotSettingsAction()
    data class AttrsChange(val attrs: String) : PotSettingsAction()

    data class ShowEditDialog(val inputValue: String) : PotSettingsAction()
    data object HideEditDialog : PotSettingsAction()

}