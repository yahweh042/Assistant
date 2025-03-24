package io.github.merlin.assistant.ui.screen.function.jiange

sealed class JianGeAction {

    data class Query(val isSpecial: Int) : JianGeAction()
    data object Begin: JianGeAction()
    data object HideBottomSheet : JianGeAction()


}