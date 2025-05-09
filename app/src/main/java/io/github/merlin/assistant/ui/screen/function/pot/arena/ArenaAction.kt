package io.github.merlin.assistant.ui.screen.function.pot.arena

sealed class ArenaAction {

    data object RetryQueryArena: ArenaAction()
    data class FightArena(val opp: Int): ArenaAction()

}