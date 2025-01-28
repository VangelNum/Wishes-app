package com.vangelnum.wisher.features.userwishviewhistory.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.userwishviewhistory.data.api.ViewHistoryApi
import com.vangelnum.wisher.features.userwishviewhistory.data.model.ViewHistory
import com.vangelnum.wisher.features.userwishviewhistory.domain.repository.ViewHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ViewHistoryRepositoryImpl @Inject constructor(
    private val api: ViewHistoryApi,
    private val errorParser: ErrorParser
) : ViewHistoryRepository {
    override fun getViewHistory(wishId: Int): Flow<UiState<List<ViewHistory>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getViewHistory(wishId)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }
}