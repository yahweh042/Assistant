package io.github.merlin.assistant.ui.screen.function.pot.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class PotHomeViewModel @Inject constructor(
    private val potRepo: PotRepo
) : AbstractViewModel<PotHomeUiState, PotHomeEvent, PotHomeAction>(
    initialState = run {
        PotHomeUiState()
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

    override fun handleAction(action: PotHomeAction) {
        when (action) {
            is PotHomeAction.BeginChallengeLevelJob -> handleBeginChallengeLevel(action)
            is PotHomeAction.BeginChallengeBossJob -> handleBeginChallengeBoss(action)
            PotHomeAction.EndJob -> handleEndJob()
            PotHomeAction.RefreshPotInfo -> handleRefreshPotIndex()
            PotHomeAction.BeginAdventureJob -> handleBeginAdventure()
            PotHomeAction.HideMysteryDialog -> handleHideMysteryDialog()
            PotHomeAction.ShowMysteryDialog -> handleShowMysteryDialog()
            is PotHomeAction.SwitchMystery -> handleSwitchMystery(action)
            is PotHomeAction.Decompose -> handleDecompose(action)
            is PotHomeAction.Equip -> handleEquip(action)
            is PotHomeAction.GetAward -> handleGetAward(action)
            is PotHomeAction.UpgradeSlot -> handleUpgradeSlot(action)
            PotHomeAction.HideBottomSheet -> handleHideBottomSheet()
        }
    }

    private fun handleRefreshPotIndex() {
        mutableStateFlow.update {
            it.copy(viewState = ViewState.Loading)
        }
        receivePotIndex()
    }

    private fun handleSwitchMystery(action: PotHomeAction.SwitchMystery) {
        when (val mysteryDialogState = state.mysteryDialogState) {
            is PotHomeUiState.MysteryDialogState.Show -> mutableStateFlow.update {
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

    private fun handleUpgradeSlot(action: PotHomeAction.UpgradeSlot) {
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
                            viewState = ViewState.Success(upgradeResult.toPotInfo()),
                            logs = it.logs.plus("第${count.incrementAndGet()}次 强化成功")
                        )
                    }
                    break
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            viewState = ViewState.Success(upgradeResult.toPotInfo()),
                            logs = it.logs.plus("第${count.incrementAndGet()}次 强化失败 祝福+${upgradeResult.addBlessing}")
                        )
                    }
                }
            }
            mutableStateFlow.update { it.copy(jobbing = false) }
        }
    }

    private fun handleGetAward(action: PotHomeAction.GetAward) {
        viewModelScope.launch {
            val getAwardResponse = potRepo.getAward(action.type)
            sendEvent(PotHomeEvent.ShowToast("领取 ${getAwardResponse.award}"))
        }
    }

    private fun handleEquip(action: PotHomeAction.Equip) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("正在装备"))
            }
            val equipResult = potRepo.equip(action.equipmentId.toString())
            if (equipResult.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(equipResult.toPotInfo()),
                        logs = it.logs.plus("装备 ${equipResult.msg}"),
                        undisposedDialogState = PotHomeUiState.UndisposedDialogState.Hide,
                    )
                }
            } else {
                mutableStateFlow.update { it.copy(viewState = ViewState.Error(equipResult.msg)) }
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleDecompose(action: PotHomeAction.Decompose) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("正在分解"))
            }
            val decomposeResult = potRepo.decompose(action.equipmentId)
            if (decomposeResult.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(decomposeResult.toPotInfo()),
                        logs = it.logs.plus("分解 ${decomposeResult.msg}"),
                        undisposedDialogState = PotHomeUiState.UndisposedDialogState.Hide,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(decomposeResult.msg))
                }
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleBeginChallengeBoss(action: PotHomeAction.BeginChallengeBossJob) {
        job?.cancel()
        job = viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    jobbing = true,
                    mysteryDialogState = PotHomeUiState.MysteryDialogState.Hide,
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
                    it.copy(viewState = ViewState.Success(challengeResponse.toPotInfo()))
                }
                if (challengeResponse.killed == 1) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"))
                    }
                    if (challengeResponse.undisposed?.isNotEmpty() == true) {
                        mutableStateFlow.update {
                            it.copy(
                                logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"),
                                undisposedDialogState = PotHomeUiState.UndisposedDialogState.Show(
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
            it.copy(mysteryDialogState = PotHomeUiState.MysteryDialogState.Hide)
        }
    }

    private fun handleShowMysteryDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(mysteryDialogState = PotHomeUiState.MysteryDialogState.Loading)
            }
            val mysteryResponse = potRepo.queryMystery()
            if (mysteryResponse.result != 0) {
                mutableStateFlow.update {
                    it.copy(mysteryDialogState = PotHomeUiState.MysteryDialogState.Hide)
                }
                sendEvent(PotHomeEvent.ShowToast("${mysteryResponse.msg}"))
                return@launch
            }
            val map = mysteryResponse.mysteries.associateBy { it.mysteryId }
            mutableStateFlow.update {
                it.copy(
                    mysteryDialogState = PotHomeUiState.MysteryDialogState.Show(
                        curMysteryId = map.firstNotNullOf { entry -> entry.key },
                        mysteries = map,
                    )
                )
            }
        }
    }

    private fun handleBeginChallengeLevel(action: PotHomeAction.BeginChallengeLevelJob) {
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
                    it.copy(viewState = ViewState.Success(challengeResponse.toPotInfo()))
                }
                if (challengeResponse.passed == 1) {
                    mutableStateFlow.update {
                        it.copy(logs = it.logs.plus("第${count.incrementAndGet()}次 挑战成功"))
                    }
                    if (challengeResponse.undisposed?.isNotEmpty() == true) {
                        mutableStateFlow.update {
                            it.copy(
                                logs = it.logs.plus("请处理装备 ${challengeResponse.undisposed[0].name}"),
                                undisposedDialogState = PotHomeUiState.UndisposedDialogState.Show(
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
                                    viewState = ViewState.Success(decomposeResult.toPotInfo()),
                                    logs = it.logs.plus("分解 ${decomposeResult.msg}")
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(viewState = ViewState.Error(decomposeResult.msg))
                            }
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                viewState = ViewState.Success(adventureResponse.toPotInfo()),
                                logs = it.logs.plus("请处理装备 ${undisposed.name}"),
                                undisposedDialogState = PotHomeUiState.UndisposedDialogState.Show(
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
        if (undisposed.level == 30) {
            return false
        }
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
                it.copy(viewState = ViewState.Loading)
            }
            val indexResponse = potRepo.index()
            if (indexResponse.result == 0) {
                val potInfo = indexResponse.toPotInfo()
                val dialogState = if (potInfo.undisposed.isNotEmpty()) {
                    PotHomeUiState.UndisposedDialogState.Show(potInfo.undisposed[0])
                } else {
                    PotHomeUiState.UndisposedDialogState.Hide
                }
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(potInfo),
                        undisposedDialogState = dialogState,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(indexResponse.msg))
                }
            }

        }
    }

}