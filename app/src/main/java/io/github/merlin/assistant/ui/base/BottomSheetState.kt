package io.github.merlin.assistant.ui.base

sealed class BottomSheetState {

    data object Show: BottomSheetState()
    data object Hide: BottomSheetState()

}