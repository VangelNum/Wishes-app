package com.vangelnum.wisher.features.home.sendwish.selectholiday.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.api.HolidayApi
import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.model.Holiday
import com.vangelnum.wisher.features.home.sendwish.selectholiday.domain.repository.HolidayRepository
import com.vangelnum.wisher.features.translate.data.model.api.TranslateApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.util.Locale
import javax.inject.Inject

class HolidayRepositoryImpl @Inject constructor(
    private val api: HolidayApi,
    private val translateApi: TranslateApi
) : HolidayRepository {
    override fun getHolidays(date: String): Flow<UiState<List<Holiday>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getHolidays(date)

            val holidayLanguageCode = "ru"
            val userLanguageCode = Locale.getDefault().language

            if (holidayLanguageCode == userLanguageCode) {
                emit(UiState.Success(response.map { Holiday(name = it.name) })) // Просто преобразуем в Holiday, если не нужно переводить
                return@flow
            }

            val holidayNames = response.map { it.name }

            val translatedNames = translateTextList(holidayNames)

            val translatedHolidays = response.mapIndexed { index, holiday ->
                Holiday(name = translatedNames[index])
            }

            emit(UiState.Success(translatedHolidays))

        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }

    private suspend fun translateTextList(texts: List<String>): List<String> {
        val holidayLanguageCode = "ru"
        val userLanguageCode = Locale.getDefault().language
        if (holidayLanguageCode == userLanguageCode) return texts

        return translateApi.translateText(
            text = texts.joinToString("\n"),
            langpair = "$holidayLanguageCode|$userLanguageCode"
        ).split("\n")
    }
}