package com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.data.api.WorldTimeApi
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import com.vangelnum.wisher.features.home.sendwish.selectdate.worldtime.domain.repository.WorldTimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class WorldTimeRepositoryImpl @Inject constructor(
    private val worldTimeApi: WorldTimeApi
) : WorldTimeRepository {
    override fun getCurrentDate(): Flow<UiState<DateInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = worldTimeApi.getCurrentDate()
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred ${e.message}"))
        }
    }
}