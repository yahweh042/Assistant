package io.github.merlin.assistant.ui.screen.function.pot

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class PotViewModel @Inject constructor(
    private val potRepo: PotRepo
) : AbstractViewModel<PotUiState, PotEvent, PotAction>(
    initialState = run {
        PotUiState()
    }
) {

    private var job: Job? = null

    init {
        receivePotIndex()
    }

    override fun handleAction(action: PotAction) {
        when (action) {
            is PotAction.BeginChallengeLevelJob -> handleBeginChallengeLevel(action)
            is PotAction.BeginChallengeBossJob -> handleBeginChallengeBoss(action)
            PotAction.EndJob -> handleEndJob()
            PotAction.RefreshPotInfo -> receivePotIndex()
            PotAction.BeginAdventureJob -> handleBeginAdventure()
            PotAction.HideChooserDialog -> handleHideChooserDialog()
            is PotAction.ShowChooserDialog -> handleShowChooserDialog(action)
            is PotAction.Decompose -> handleDecompose(action)
            is PotAction.Equip -> handleEquip(action)
            is PotAction.GetAward -> handleGetAward(action)
        }
    }

    private fun handleGetAward(action: PotAction.GetAward) {
        viewModelScope.launch {
            val getAwardResponse = potRepo.getAward(action.type)
            mutableStateFlow.update {
                it.copy(logs = it.logs.plus("领取 ${getAwardResponse.award}"))
            }
        }
    }

    private fun handleEquip(action: PotAction.Equip) {
        viewModelScope.launch {
            val equipResult = potRepo.equip(action.equipmentId.toString())
            if (equipResult.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        potDetailViewState = ViewState.Success(equipResult.toPotInfo()),
                        logs = it.logs.plus("装备 ${equipResult.msg}")
                    )
                }
            } else {
                mutableStateFlow.update { it.copy(potDetailViewState = ViewState.Error(equipResult.msg)) }
            }
        }
    }

    private fun handleDecompose(action: PotAction.Decompose) {
        viewModelScope.launch {
            val decomposeResult = potRepo.decompose(action.equipmentId.toString())
            if (decomposeResult.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        potDetailViewState = ViewState.Success(decomposeResult.toPotInfo()),
                        logs = it.logs.plus("分解 ${decomposeResult.msg}")
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        potDetailViewState = ViewState.Error(
                            decomposeResult.msg
                        )
                    )
                }
            }
        }
    }

    private fun handleBeginChallengeBoss(action: PotAction.BeginChallengeBossJob) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    chooserDialogState = PotUiState.ChooserDialogState.Hide,
                    logs = listOf(),
                )
            }
            val indexResponse = potRepo.index()
            if (indexResponse.result != 0) {
                mutableStateFlow.update {
                    it.copy(logs = it.logs.plus("未知错误 ${indexResponse.msg}"))
                }
                return@launch
            }
            val count = AtomicInteger(0)
            var running = true
            while (running) {
                mutableStateFlow.update {
                    it.copy(logs = it.logs.plus("开始第${count.incrementAndGet()}轮次"))
                }
                val challengeResponse = potRepo.challengeBoss(action.bossId.toString())
                if (challengeResponse.result == 0) {
                    mutableStateFlow.update {
                        it.copy(potDetailViewState = ViewState.Success(challengeResponse.toPotInfo()))
                    }
                    if (challengeResponse.passed == 1) {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("成功"))
                        }
                        challengeResponse.undisposed?.let {
                            running = false
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("挑战失败 ${challengeResponse.msg}"))
                        }
                    }

                } else {
                    if ("请先处理上次掉落的装备" == challengeResponse.msg) {
                        running = false
                    }
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("失败 ${challengeResponse.msg}"))
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }
    }

    private fun handleHideChooserDialog() {
        mutableStateFlow.update {
            it.copy(chooserDialogState = PotUiState.ChooserDialogState.Hide)
        }
    }

    private fun handleShowChooserDialog(action: PotAction.ShowChooserDialog) {
        mutableStateFlow.update {
            it.copy(chooserDialogState = PotUiState.ChooserDialogState.Show(action.type))
        }
    }

    private fun handleBeginChallengeLevel(action: PotAction.BeginChallengeLevelJob) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    chooserDialogState = PotUiState.ChooserDialogState.Hide,
                    logs = listOf(),
                )
            }
            val indexResponse = potRepo.index()
            if (indexResponse.result != 0) {
                mutableStateFlow.update {
                    it.copy(logs = it.logs.plus("未知错误 ${indexResponse.msg}"))
                }
                return@launch
            }
            val count = AtomicInteger(0)
            var running = true
            while (running) {
                mutableStateFlow.update {
                    it.copy(logs = it.logs.plus("开始第${count.incrementAndGet()}轮次"))
                }
                val challengeResponse = potRepo.challengeLevel(action.levelId.toString())
                if (challengeResponse.result == 0) {
                    mutableStateFlow.update {
                        it.copy(potDetailViewState = ViewState.Success(challengeResponse.toPotInfo()))
                    }
                    if (challengeResponse.passed == 1) {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("成功"))
                        }
                        challengeResponse.undisposed?.let {
                            running = false
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("挑战失败 ${challengeResponse.msg}"))
                        }
                    }

                } else {
                    if ("请先处理上次掉落的装备" == challengeResponse.msg || "该关卡已经通过啦" == challengeResponse.msg) {
                        running = false
                    }
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("失败 ${challengeResponse.msg}"))
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }

    }

    private fun handleBeginAdventure() {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(jobbing = true, logs = listOf())
            }
            val count = AtomicInteger(0)
            var running = true
            while (running) {
                mutableStateFlow.update {
                    it.copy(logs = it.logs.plus("开始第${count.incrementAndGet()}轮次"))
                }
                val adventureResponse = potRepo.adventure()
                if (adventureResponse.result == 0) {
                    mutableStateFlow.update {
                        it.copy(potDetailViewState = ViewState.Success(adventureResponse.toPotInfo()))
                    }
                    adventureResponse.undisposed?.let {
                        running = false
                    }
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("挑战成功 ${adventureResponse.msg}"))
                    }

                } else {
                    if ("请先处理上次掉落的装备" == adventureResponse.msg) {
                        running = false
                    }
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("失败 ${adventureResponse.msg}"))
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }
    }

    private fun handleEndJob() {
        job?.cancel()
        mutableStateFlow.update {
            it.copy(jobbing = false)
        }
    }

    private fun receivePotIndex() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(potDetailViewState = ViewState.Loading)
            }
            val indexResponse = potRepo.index()
            if (indexResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(potDetailViewState = ViewState.Success(indexResponse.toPotInfo()))
                }
            } else {
                mutableStateFlow.update {
                    it.copy(potDetailViewState = ViewState.Error(indexResponse.msg))
                }
            }

        }
    }

}