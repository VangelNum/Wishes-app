package com.vangelnum.wishes.features.keylogshistory.data.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.keylogshistory.data.api.KeyLogsHistoryApi
import com.vangelnum.wishes.features.keylogshistory.data.model.KeyLogsHistory
import com.vangelnum.wishes.features.keylogshistory.domain.repository.KeyLogsHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class KeyLogsHistoryRepositoryImpl @Inject constructor(
    private val api: KeyLogsHistoryApi,
    private val errorParser: ErrorParser
) : KeyLogsHistoryRepository {
    override fun getKeyLogsHistory(): Flow<UiState<List<KeyLogsHistory>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getKeyLogsHistory()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }
}