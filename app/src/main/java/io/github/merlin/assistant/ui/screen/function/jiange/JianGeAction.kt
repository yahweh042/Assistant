package io.github.merlin.assistant.ui.screen.function.jiange

sealed class JianGeAction {

    data object Query : JianGeAction()
    data object Begin: JianGeAction()
    data object HideBottomSheet : JianGeAction()


}