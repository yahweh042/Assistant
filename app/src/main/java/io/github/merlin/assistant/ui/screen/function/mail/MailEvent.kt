package io.github.merlin.assistant.ui.screen.function.mail

sealed class MailEvent {
    data class ShowToast(val msg: String) : MailEvent()
}