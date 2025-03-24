package io.github.merlin.assistant.ui.screen.function.jiange

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.JianGeService
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JianGeViewModel @Inject constructor(
    private val jianGeService: JianGeService
) : AbstractViewModel<JianGeUiState, JianGeEvent, JianGeAction>(
    initialState = run {
        JianGeUiState()
    }
) {

    init {
        query()
    }

    private var job: Job? = null

    override fun handleAction(action: JianGeAction) {
        when (action) {
            is JianGeAction.Query -> query(action.isSpecial)
            JianGeAction.Begin -> handleBegin(action)
            JianGeAction.HideBottomSheet -> handleHideBottomSheet(action)
        }
    }

    private fun handleHideBottomSheet(action: JianGeAction) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(showBottomSheet = false)
            }
        }
    }

    private fun handleBegin(action: JianGeAction) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(showBottomSheet = true, logs = listOf())
            }
            while (true) {
                val queryResponse = jianGeService.query(0)
                if (queryResponse.result != 0) {
                    mutableStateFlow.update {
                        it.copy(viewState = ViewState.Error(queryResponse.msg ?: "未知错误"))
                    }
                    break
                }
                mutableStateFlow.update { it.copy(viewState = ViewState.Success(queryResponse.toViewState())) }
                if (queryResponse.isGetAward == 1) {
                    passLevelAwardGet()
                }
                if (queryResponse.highestPassFloor == null) {
                    continue
                }
                val levelId = queryResponse.highestPassFloor + 1
                jianGeService.levelDetail(levelId)
                val levelChallenge = jianGeService.levelChallenge(levelId)
                mutableStateFlow.update { it.copy(logs = it.logs.plus("挑战第${levelId}层 ${levelChallenge.msg}")) }
                if (levelChallenge.result != 0 || levelChallenge.win == 0) {
                    break
                }
            }
        }
    }

    private suspend fun passLevelAwardGet() {
        jianGeService.passLevelAwardGet(0, 1)
        jianGeService.passLevelAwardClose()
    }

    private fun query(isSpecial: Int = 0) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            val queryResponse = jianGeService.query(isSpecial = isSpecial)
            if (queryResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Success(queryResponse.toViewState()))
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(queryResponse.msg ?: "未知错误"))
                }
            }
        }
    }

}