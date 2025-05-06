package io.github.merlin.assistant.ui.screen.function.mail

sealed class MailAction {
    data class QueryMails(val type: Int = 2) : MailAction()
    data object RetryQueryMails : MailAction()
    data class OpenMail(val id: Int) : MailAction()
    data class GetReward(val id: Int) : MailAction()

    data object HideSheet : MailAction()
}