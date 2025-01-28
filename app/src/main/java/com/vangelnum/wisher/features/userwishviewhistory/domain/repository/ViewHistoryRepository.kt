package com.vangelnum.wisher.features.userwishviewhistory.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.userwishviewhistory.data.model.ViewHistory
import kotlinx.coroutines.flow.Flow

interface ViewHistoryRepository {
    fun getViewHistory(wishId: Int): Flow<UiState<List<ViewHistory>>>
}