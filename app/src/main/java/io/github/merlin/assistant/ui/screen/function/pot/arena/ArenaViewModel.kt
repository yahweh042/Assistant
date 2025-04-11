package io.github.merlin.assistant.ui.screen.function.pot.arena

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArenaViewModel @Inject constructor(
    val potRepo: PotRepo
) : AbstractViewModel<ArenaUiState, ArenaEvent, ArenaAction>(
    initialState = run {
        ArenaUiState()
    }
) {
    init {
        queryArena()
    }

    override fun handleAction(action: ArenaAction) {
        when (action) {
            is ArenaAction.FightArena -> handleFightArena(action.opp)
            ArenaAction.QueryArena -> queryArena()
        }
    }

    private fun handleFightArena(opp: Int) {
        viewModelScope.launch {
            val fightResponse = potRepo.fightArena(opp)
            if (fightResponse.result == 0) {
                sendEvent(ArenaEvent.ShowToast("挑战${if (fightResponse.win == 1) "成功" else "失败"}"))
                queryArena(false)
            } else {
                sendEvent(ArenaEvent.ShowToast("${fightResponse.msg}"))
            }
        }
    }

    private fun queryArena(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                mutableStateFlow.update { it.copy(viewState = ViewState.Loading) }
            }
            val queryResponse = potRepo.queryArena()
            if (queryResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(
                            ArenaUiState.ArenaState(
                                freeTimes = queryResponse.freeTimes,
                                selfRank = queryResponse.selfRank,
                                totalPoint = queryResponse.totalPoint,
                                oppInfo = queryResponse.oppInfo,
                            )
                        )
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error("${queryResponse.msg}"))
                }
            }
        }
    }
}