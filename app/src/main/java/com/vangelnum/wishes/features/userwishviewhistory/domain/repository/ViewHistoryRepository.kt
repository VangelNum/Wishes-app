package com.vangelnum.wishes.features.userwishviewhistory.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.userwishviewhistory.data.model.ViewHistory
import kotlinx.coroutines.flow.Flow

interface ViewHistoryRepository {
    fun getViewHistory(wishId: Int): Flow<UiState<List<ViewHistory>>>
}