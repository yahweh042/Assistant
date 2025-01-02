package io.github.merlin.assistant.ui.base

sealed class ViewState {

    data object Loading : ViewState()
    data class Success<T>(val data: T) : ViewState()
    data class Error(val msg: String) : ViewState()

}