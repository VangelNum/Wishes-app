package com.vangelnum.wisher.features.home.sendwish.stage2.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage2.data.api.HolidayApi
import com.vangelnum.wisher.features.home.sendwish.stage2.data.model.Holiday
import com.vangelnum.wisher.features.home.sendwish.stage2.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class HolidayRepositoryImpl @Inject constructor(
    private val api: HolidayApi
) : HolidayRepository {
    override fun getHolidays(date: String): Flow<UiState<List<Holiday>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getHolidays(date)
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }
}