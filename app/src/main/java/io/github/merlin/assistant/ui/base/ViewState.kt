package io.github.merlin.assistant.ui.base

sealed class ViewState<out T> {

    data object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val msg: String) : ViewState<Nothing>()

}