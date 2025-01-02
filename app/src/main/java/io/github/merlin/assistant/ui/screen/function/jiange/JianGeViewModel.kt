package io.github.merlin.assistant.ui.screen.function.jiange

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.JianGeService
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
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

    override fun handleAction(action: JianGeAction) {
        when(action) {
            JianGeAction.Query -> query()
        }
    }

    private fun query() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            val queryResponse = jianGeService.query(0)
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