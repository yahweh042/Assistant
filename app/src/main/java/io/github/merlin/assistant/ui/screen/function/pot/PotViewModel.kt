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
            is PotAction.ShowChooserDialog -> handleShowChooserDialog()
            is PotAction.Decompose -> handleDecompose(action)
            is PotAction.Equip -> handleEquip(action)
            is PotAction.GetAward -> handleGetAward(action)
            is PotAction.UpgradeSlot -> handleUpgradeSlot(action)
        }
    }

    private fun handleUpgradeSlot(action: PotAction.UpgradeSlot) {
        job?.cancel()
        job = viewModelScope.launch {
            sendEvent(PotEvent.ShowBottomSheet)
            mutableStateFlow.update {
                it.copy(jobbing = true, logs = listOf())
            }
            val count = AtomicInteger(0)
            while (true) {
                val upgradeResult = potRepo.upgradeSlot(action.type)
                if (upgradeResult.result != 0 || upgradeResult.addBlessing == null) {
                    mutableStateFlow.update { it.copy(logs = it.logs.plus("未知错误 ${upgradeResult.msg}")) }
                    break
                }
                if (upgradeResult.addBlessing == 0) {
                    mutableStateFlow.update {
                        it.copy(
                            potDetailViewState = ViewState.Success(upgradeResult.toPotInfo()),
                            logs = it.logs.plus("第${count.incrementAndGet()}次 强化成功")
                        )
                    }
                    break
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            potDetailViewState = ViewState.Success(upgradeResult.toPotInfo()),
                            logs = it.logs.plus("第${count.incrementAndGet()}次 强化失败 祝福+${upgradeResult.addBlessing}")
                        )
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }
    }

    private fun handleGetAward(action: PotAction.GetAward) {
        viewModelScope.launch {
            val getAwardResponse = potRepo.getAward(action.type)
            sendEvent(PotEvent.ShowToast("领取 ${getAwardResponse.award}"))
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
            sendEvent(PotEvent.ShowBottomSheet)
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    chooserDialogState = PotUiState.ChooserDialogState.Hide,
                    logs = listOf(),
                )
            }
            val count = AtomicInteger(0)
            while (true) {
                val challengeResponse = potRepo.challengeBoss(action.bossId.toString())
                if (challengeResponse.result != 0) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("未知错误 ${challengeResponse.msg}"))
                    }
                    break
                }
                mutableStateFlow.update {
                    it.copy(potDetailViewState = ViewState.Success(challengeResponse.toPotInfo()))
                }
                if (challengeResponse.killed == 1) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"))
                    }
                    if (challengeResponse.undisposed?.isNotEmpty() == true) {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"))
                        }
                        break
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战失败 ${challengeResponse.msg}"))
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

    private fun handleShowChooserDialog() {
        mutableStateFlow.update {
            it.copy(chooserDialogState = PotUiState.ChooserDialogState.Show)
        }
    }

    private fun handleBeginChallengeLevel(action: PotAction.BeginChallengeLevelJob) {
        job?.cancel()
        job = viewModelScope.launch {
            sendEvent(PotEvent.ShowBottomSheet)
            mutableStateFlow.update {
                it.copy(jobbing = true, logs = listOf())
            }
            val levelId = action.levelId + 1
            val count = AtomicInteger(0)
            while (true) {
                val challengeResponse = potRepo.challengeLevel(levelId.toString())
                if (challengeResponse.result != 0) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("未知错误 ${challengeResponse.msg}"))
                    }
                    break
                }
                mutableStateFlow.update {
                    it.copy(potDetailViewState = ViewState.Success(challengeResponse.toPotInfo()))
                }
                if (challengeResponse.passed == 1) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"))
                    }
                    if (challengeResponse.undisposed?.isNotEmpty() == true) {
                        mutableStateFlow.update {
                            it.copy(logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"))
                        }
                        break
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战失败 ${challengeResponse.msg}"))
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }

    }

    private fun handleBeginAdventure() {
        job?.cancel()
        job = viewModelScope.launch {
            sendEvent(PotEvent.ShowBottomSheet)
            mutableStateFlow.update {
                it.copy(jobbing = true, logs = listOf())
            }
            val count = AtomicInteger(0)
            while (true) {
                val adventureResponse = potRepo.adventure()
                if (adventureResponse.result != 0) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("未知错误 ${adventureResponse.msg}"))
                    }
                    break
                }
                mutableStateFlow.update {
                    it.copy(
                        potDetailViewState = ViewState.Success(adventureResponse.toPotInfo()),
                        logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"),
                    )
                }
                if (adventureResponse.undisposed?.isNotEmpty() == true) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("请处理装备 ${adventureResponse.undisposed[0].name}"))
                    }
                    break
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
        sendEvent(PotEvent.HideBottomSheet)
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