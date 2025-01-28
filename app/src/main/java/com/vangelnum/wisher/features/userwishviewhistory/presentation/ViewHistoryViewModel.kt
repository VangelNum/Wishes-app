package com.vangelnum.wisher.features.userwishviewhistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.userwishviewhistory.data.model.ViewHistory
import com.vangelnum.wisher.features.userwishviewhistory.domain.repository.ViewHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewHistoryViewModel @Inject constructor(
    private val repository: ViewHistoryRepository
) : ViewModel() {
    private val _viewHistoryState = MutableStateFlow<UiState<List<ViewHistory>>>(UiState.Idle())
    val viewHistoryState = _viewHistoryState.asStateFlow()

    fun onEvent(event: ViewHistoryEvent) {
        when (event) {
            is ViewHistoryEvent.OnGetViewHistory -> getViewHistory(event.wishId)
        }
    }

    private fun getViewHistory(wishId: Int) {
        viewModelScope.launch {
            repository.getViewHistory(wishId).collect { state->
                _viewHistoryState.update { state }
            }
        }
    }
}