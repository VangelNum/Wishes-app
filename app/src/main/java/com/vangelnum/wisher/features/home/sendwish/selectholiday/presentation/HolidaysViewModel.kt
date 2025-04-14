package com.vangelnum.wisher.features.home.sendwish.selectholiday.presentation

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.model.Holiday
import com.vangelnum.wisher.features.home.sendwish.selectholiday.domain.repository.HolidayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _holidayUiState = MutableStateFlow<UiState<List<Holiday>>>(UiState.Idle())
    val holidayUiState = _holidayUiState.asStateFlow()

    companion object {
        const val HOLIDAY_DATE_ARG = "holidayDate"
    }

    init {
        val holidayDate = savedStateHandle.get<String>(HOLIDAY_DATE_ARG)
        if (holidayDate != null) {
            getHolidays(holidayDate)
        } else {
            _holidayUiState.update { UiState.Error("Holiday date argument is missing") }
        }
    }

    fun getHolidays(date: String) {
        val locale = Locale.current.language
        viewModelScope.launch {
            holidayRepository.getHolidays(date, locale).collectLatest { state ->
                _holidayUiState.update {
                    state
                }
            }
        }
    }
}