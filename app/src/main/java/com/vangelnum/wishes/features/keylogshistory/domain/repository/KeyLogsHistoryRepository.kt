package com.vangelnum.wishes.features.keylogshistory.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.keylogshistory.data.model.KeyLogsHistory
import kotlinx.coroutines.flow.Flow

interface KeyLogsHistoryRepository {
    fun getKeyLogsHistory(): Flow<UiState<List<KeyLogsHistory>>>
}