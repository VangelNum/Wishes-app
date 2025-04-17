package com.vangelnum.wishes.features.keylogshistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.keylogshistory.data.model.KeyLogsHistory
import com.vangelnum.wishes.features.keylogshistory.domain.repository.KeyLogsHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyLogsHistoryViewModel @Inject constructor(
    private val repository: KeyLogsHistoryRepository
): ViewModel() {
    private val _keyLogsHistoryState = MutableStateFlow<UiState<List<KeyLogsHistory>>>(UiState.Idle())
    val keyLogsHistoryState = _keyLogsHistoryState.asStateFlow()

    init {
        onEvent(KeyLogsHistoryEvent.OnGetKeyLogsHistory)
    }

    fun onEvent(event: KeyLogsHistoryEvent) {
        when (event) {
            KeyLogsHistoryEvent.OnGetKeyLogsHistory -> getKeyLogsHistory()
        }
    }

    private fun getKeyLogsHistory() {
        viewModelScope.launch {
            repository.getKeyLogsHistory().collect { state->
                _keyLogsHistoryState.update { state }
            }
        }
    }
}