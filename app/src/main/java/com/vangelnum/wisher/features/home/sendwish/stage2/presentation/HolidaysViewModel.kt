package com.vangelnum.wisher.features.home.sendwish.stage2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage2.data.model.Holiday
import com.vangelnum.wisher.features.home.sendwish.stage2.domain.repository.HolidayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository
) : ViewModel() {
    private val _holidayUiState = MutableStateFlow<UiState<List<Holiday>>>(UiState.Idle())
    val holidayUiState = _holidayUiState.asStateFlow()

    fun onEvent(event: HolidaysEvent) {
        when (event) {
            is HolidaysEvent.GetHolidays -> {
                getHolidays(event.date)
            }
        }
    }

    private fun getHolidays(date: String) {
        viewModelScope.launch {
            holidayRepository.getHolidays(date).collectLatest { state->
                _holidayUiState.update {
                    state
                }
            }
        }
    }
}