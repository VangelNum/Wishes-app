package com.vangelnum.wisher.features.userwishsendinghistory.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.userwishsendinghistory.data.api.UserWishesHistoryApi
import com.vangelnum.wisher.features.userwishsendinghistory.domain.repository.UserWishesHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserWishesHistoryRepositoryImpl @Inject constructor(
    private val api: UserWishesHistoryApi,
    private val errorParser: ErrorParser
) : UserWishesHistoryRepository {
    override fun getMyWishes(): Flow<UiState<List<Wish>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getMyWishes()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }
}