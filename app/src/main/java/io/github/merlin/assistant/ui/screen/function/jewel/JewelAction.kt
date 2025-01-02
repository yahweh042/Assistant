package io.github.merlin.assistant.ui.screen.function.jewel

sealed class JewelAction {
    data object ShowSheet : JewelAction()
    data object HideSheet : JewelAction()

    data object RunTask : JewelAction()
    data class SwitchBuyVit(val buyVit: Boolean) : JewelAction()
    data class InputFacName(val facName: String) : JewelAction()
    data class InputRoleName(val roleName: String) : JewelAction()
    data class ChooseType(val type: Int) : JewelAction()
    data class InputChooseSecretReel(val chooseSecretReel: String) : JewelAction()
    data object ResetForm : JewelAction()
    data object SubmitForm : JewelAction()
}