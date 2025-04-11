package io.github.merlin.assistant.ui.screen.function.pot.arena

import io.github.merlin.assistant.data.network.response.QueryArenaResponse.OppInfo
import io.github.merlin.assistant.ui.base.ViewState

data class ArenaUiState(
    val viewState: ViewState = ViewState.Loading,
) {

    data class ArenaState(
        val freeTimes: Int = 0,
        val selfRank: Int = 0,
        val totalPoint: Int = 0,
        val oppInfo: List<OppInfo> = listOf(),
    )

}
