package com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import kotlinx.coroutines.flow.Flow

interface WorldTimeRepository {
    fun getCurrentDate(): Flow<UiState<DateInfo>>
}