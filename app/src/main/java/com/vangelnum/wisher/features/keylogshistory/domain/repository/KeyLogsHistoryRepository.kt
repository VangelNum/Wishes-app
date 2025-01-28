package com.vangelnum.wisher.features.keylogshistory.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.keylogshistory.data.model.KeyLogsHistory
import kotlinx.coroutines.flow.Flow

interface KeyLogsHistoryRepository {
    fun getKeyLogsHistory(): Flow<UiState<List<KeyLogsHistory>>>
}