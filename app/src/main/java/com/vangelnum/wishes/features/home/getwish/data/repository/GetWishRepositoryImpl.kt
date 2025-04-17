package com.vangelnum.wishes.features.home.getwish.data.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.home.getwish.data.api.GetWishApi
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wishes.features.home.getwish.domain.repository.GetWishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWishRepositoryImpl @Inject constructor(
    private val api: GetWishApi,
    private val errorParser: ErrorParser
) : GetWishRepository {

    override fun getDatesByKey(key: String): Flow<UiState<List<WishDatesInfo>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getWishesDatesByKey(key)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun getWishes(key: String, id: Int): Flow<UiState<Wish>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getWishes(key, id)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun getLastWishByKey(key: String): Flow<UiState<Wish>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getLastWishByKey(key)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }
}