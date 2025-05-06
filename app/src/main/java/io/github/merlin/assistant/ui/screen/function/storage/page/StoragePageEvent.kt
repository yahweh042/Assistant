package io.github.merlin.assistant.ui.screen.function.storage.page

sealed class StoragePageEvent {

    data class ShowToast(val msg: String): StoragePageEvent()

}