package io.github.merlin.assistant.repo.model

sealed class SwitchAccountResult {

    data object NoChange : SwitchAccountResult()
    data class SwitchError(val msg: String): SwitchAccountResult()
    data object Switched : SwitchAccountResult()

}