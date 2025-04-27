package io.github.merlin.assistant.ui.screen.function.jiange

import io.github.merlin.assistant.data.network.response.JianGeQueryResponse
import io.github.merlin.assistant.ui.base.ViewState

fun JianGeQueryResponse.toViewState(): JianGeUiState.QueryState {
    return JianGeUiState.QueryState(
        highestPassFloor = this.highestPassFloor ?: 0,
        isGetAward = this.isGetAward ?: 0,
        activityEndTime = this.activityEndTime ?: "",
        levelArray = this.levelArray ?: listOf(),
        isSpecial = this.isSpecial ?: 0,
    )
}

data class JianGeUiState(
    val viewState: ViewState<QueryState> = ViewState.Loading,
    val logs: List<String> = listOf(),
    val showBottomSheet: Boolean = false,
    val isSpecial: Int = 0,
) {

    data class QueryState(
        val highestPassFloor: Int = 0,
        val activityEndTime: String = "",
        val isGetAward: Int = 0,
        val levelArray: List<JianGeQueryResponse.LevelArray> = listOf(),
        val isSpecial: Int = 0,
    )

}
