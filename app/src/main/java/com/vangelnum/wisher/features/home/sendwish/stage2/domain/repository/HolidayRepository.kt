package com.vangelnum.wisher.features.home.sendwish.stage2.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage2.data.model.Holiday
import kotlinx.coroutines.flow.Flow

interface HolidayRepository {
    fun getHolidays(date: String): Flow<UiState<List<Holiday>>>
}