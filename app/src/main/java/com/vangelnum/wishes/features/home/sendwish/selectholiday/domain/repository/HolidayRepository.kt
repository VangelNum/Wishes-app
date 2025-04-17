package com.vangelnum.wishes.features.home.sendwish.selectholiday.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.sendwish.selectholiday.data.model.Holiday
import kotlinx.coroutines.flow.Flow

interface HolidayRepository {
    fun getHolidays(date: String): Flow<UiState<List<Holiday>>>
}