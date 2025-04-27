package io.github.merlin.assistant.ui.screen.function.pot.arena

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
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
            ArenaAction.RetryQueryArena -> handleRetryQueryArena()
        }
    }

    private fun handleFightArena(opp: Int) {
        mutableStateFlow.update {
            it.copy(loadingDialogState = LoadingDialogState.Loading("挑战中..."))
        }
        viewModelScope.launch {
            val fightResponse = potRepo.fightArena(opp)
            if (fightResponse.result == 0) {
                sendEvent(ArenaEvent.ShowToast("挑战${if (fightResponse.win == 1) "成功" else "失败"}"))
                queryArena()
            } else {
                sendEvent(ArenaEvent.ShowToast("${fightResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleRetryQueryArena() {
        mutableStateFlow.update {
            it.copy(viewState = ViewState.Loading)
        }
        queryArena()
    }

    private fun queryArena() {
        viewModelScope.launch {
            potRepo.signUpArena()
            val queryResponse = potRepo.queryArena()
            if (queryResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(
                            ArenaUiState.ArenaState(
                                freeTimes = queryResponse.freeTimes,
                                selfRank = queryResponse.selfRank,
                                totalPoint = queryResponse.totalPoint,
                                oppInfo = queryResponse.oppInfo.sortedBy { opp -> opp.rank },
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