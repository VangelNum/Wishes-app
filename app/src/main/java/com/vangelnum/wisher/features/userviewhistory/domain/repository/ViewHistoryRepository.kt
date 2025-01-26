package com.vangelnum.wisher.features.userviewhistory.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.userviewhistory.data.model.ViewHistory
import kotlinx.coroutines.flow.Flow

interface ViewHistoryRepository {
    fun getViewHistory(wishId: Int): Flow<UiState<List<ViewHistory>>>
}