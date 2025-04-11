package io.github.merlin.assistant.ui.screen.function.pot

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        receivePotSettings()
    }

    private fun receivePotSettings() {
        potRepo.potSettingsStateFlow
            .onEach { settings -> mutableStateFlow.update { it.copy(potSettings = settings) } }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: PotAction) {
        when (action) {
            is PotAction.BeginChallengeLevelJob -> handleBeginChallengeLevel(action)
            is PotAction.BeginChallengeBossJob -> handleBeginChallengeBoss(action)
            PotAction.EndJob -> handleEndJob()
            PotAction.RefreshPotInfo -> receivePotIndex()
            PotAction.BeginAdventureJob -> handleBeginAdventure()
            PotAction.HideMysteryDialog -> handleHideMysteryDialog()
            PotAction.ShowMysteryDialog -> handleShowMysteryDialog()
            is PotAction.SwitchMystery -> handleSwitchMystery(action)
            is PotAction.Decompose -> handleDecompose(action)
            is PotAction.Equip -> handleEquip(action)
            is PotAction.GetAward -> handleGetAward(action)
            is PotAction.UpgradeSlot -> handleUpgradeSlot(action)
            PotAction.HideBottomSheet -> handleHideBottomSheet()
        }
    }

    private fun handleSwitchMystery(action: PotAction.SwitchMystery) {
        when (val mysteryDialogState = state.mysteryDialogState) {
            is PotUiState.MysteryDialogState.Show -> mutableStateFlow.update {
                it.copy(mysteryDialogState = mysteryDialogState.copy(curMysteryId = action.mysteryId))
            }

            else -> Unit

        }
    }

    private fun handleHideBottomSheet() {
        job?.cancel()
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(showBottomSheet = false, jobbing = false)
            }
        }
    }

    private fun handleUpgradeSlot(action: PotAction.UpgradeSlot) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    logs = listOf(),
                    showBottomSheet = true,
                )
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
                        logs = it.logs.plus("装备 ${equipResult.msg}"),
                        undisposedDialogState = PotUiState.UndisposedDialogState.Hide,
                    )
                }
            } else {
                mutableStateFlow.update { it.copy(potDetailViewState = ViewState.Error(equipResult.msg)) }
            }
        }
    }

    private fun handleDecompose(action: PotAction.Decompose) {
        viewModelScope.launch {
            val decomposeResult = potRepo.decompose(action.equipmentId)
            if (decomposeResult.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        potDetailViewState = ViewState.Success(decomposeResult.toPotInfo()),
                        logs = it.logs.plus("分解 ${decomposeResult.msg}"),
                        undisposedDialogState = PotUiState.UndisposedDialogState.Hide,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(potDetailViewState = ViewState.Error(decomposeResult.msg))
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
                    mysteryDialogState = PotUiState.MysteryDialogState.Hide,
                    logs = listOf(),
                    showBottomSheet = true,
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
                            it.copy(
                                logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"),
                                undisposedDialogState = PotUiState.UndisposedDialogState.Show(
                                    challengeResponse.undisposed[0]
                                )
                            )
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

    private fun handleHideMysteryDialog() {
        mutableStateFlow.update {
            it.copy(mysteryDialogState = PotUiState.MysteryDialogState.Hide)
        }
    }

    private fun handleShowMysteryDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(mysteryDialogState = PotUiState.MysteryDialogState.Loading)
            }
            val mysteryResponse = potRepo.queryMystery()
            if (mysteryResponse.result != 0) {
                mutableStateFlow.update {
                    it.copy(mysteryDialogState = PotUiState.MysteryDialogState.Hide)
                }
                sendEvent(PotEvent.ShowToast("${mysteryResponse.msg}"))
                return@launch
            }
            val map = mysteryResponse.mysteries.associateBy { it.mysteryId }
            mutableStateFlow.update {
                it.copy(
                    mysteryDialogState = PotUiState.MysteryDialogState.Show(
                        curMysteryId = map.firstNotNullOf { entry -> entry.key },
                        mysteries = map,
                    )
                )
            }
        }
    }

    private fun handleBeginChallengeLevel(action: PotAction.BeginChallengeLevelJob) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    logs = listOf(),
                    showBottomSheet = true,
                )
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
                            it.copy(
                                logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"),
                                undisposedDialogState = PotUiState.UndisposedDialogState.Show(
                                    challengeResponse.undisposed[0]
                                ),
                            )
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
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    logs = listOf(),
                    showBottomSheet = true,
                )
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
                    it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"))
                }
                if (adventureResponse.undisposed?.isNotEmpty() == true) {
                    val undisposed = adventureResponse.undisposed[0]
                    if (shouldDecomposeEquip(undisposed)) {
                        val decomposeResult = potRepo.decompose(undisposed.equipmentId)
                        if (decomposeResult.result == 0) {
                            mutableStateFlow.update {
                                it.copy(
                                    potDetailViewState = ViewState.Success(decomposeResult.toPotInfo()),
                                    logs = it.logs.plus("分解 ${decomposeResult.msg}")
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(potDetailViewState = ViewState.Error(decomposeResult.msg))
                            }
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                potDetailViewState = ViewState.Success(adventureResponse.toPotInfo()),
                                logs = it.logs.plus("请处理装备 ${undisposed.name}"),
                                undisposedDialogState = PotUiState.UndisposedDialogState.Show(
                                    undisposed
                                )
                            )
                        }
                        break
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }
    }

    private fun shouldDecomposeEquip(undisposed: PotResponse.Equipment): Boolean {
        val potSettings = state.potSettings
        if (!potSettings.attrFilter) {
            return false
        }
        // val equiped = undisposed.equipped
        val subAttrs = undisposed.subAttrs
        for (attr in potSettings.attrs.split(",")) {
            if (subAttrs.contains(attr)) {
                return false
            }
        }
        return true
    }

    private fun handleEndJob() {
        job?.cancel()
        mutableStateFlow.update {
            it.copy(
                jobbing = false,
            )
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