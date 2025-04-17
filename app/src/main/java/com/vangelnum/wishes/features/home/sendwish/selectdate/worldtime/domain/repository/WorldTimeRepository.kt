package com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import kotlinx.coroutines.flow.Flow

interface WorldTimeRepository {
    fun getCurrentDate(): Flow<UiState<DateInfo>>
}