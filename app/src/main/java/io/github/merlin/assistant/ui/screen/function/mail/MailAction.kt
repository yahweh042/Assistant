package io.github.merlin.assistant.ui.screen.function.mail

sealed class MailAction {
    data class GetMails(val type: Int = 2) : MailAction()
}