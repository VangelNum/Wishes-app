package com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.domain.repository.WorldTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorldTimeViewModel @Inject constructor(
    private val worldTimeRepository: WorldTimeRepository
): ViewModel() {
    private val _currentDateUiState = MutableStateFlow<UiState<DateInfo>>(UiState.Idle())
    val currentDateUiState = _currentDateUiState.asStateFlow()

    init {
        getDate()
    }

    private fun getDate() {
        viewModelScope.launch {
            worldTimeRepository.getCurrentDate().collectLatest { state->
                _currentDateUiState.update { state }
            }
        }
    }
}