package com.vangelnum.wisher.features.home.sendwish.selectholiday.data.repository

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.api.HolidayApi
import com.vangelnum.wisher.features.home.sendwish.selectholiday.data.model.Holiday
import com.vangelnum.wisher.features.home.sendwish.selectholiday.domain.repository.HolidayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

class HolidayRepositoryImpl @Inject constructor(
    private val api: HolidayApi
) : HolidayRepository {
    override fun getHolidays(date: String, languageCode: String): Flow<UiState<List<Holiday>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getHolidays(date)
            val translatedHolidays = mutableListOf<Holiday>()
            for (holiday in response) {
                val translatedName = translateText(holiday.name, languageCode)
                translatedHolidays.add(Holiday(name = translatedName))
            }
            emit(UiState.Success(translatedHolidays))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }

    private suspend fun translateText(text: String, targetLanguageCode: String): String {
        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.RUSSIAN)
                .setTargetLanguage(targetLanguageCode)
                .build()
            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded().await()
            translator.translate(text).await()
        } catch (e: Exception) {
            e.printStackTrace()
            return text
        }
    }
}