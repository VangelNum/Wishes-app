package com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository

import android.net.Uri
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.SendWishRequest
import kotlinx.coroutines.flow.Flow

interface SendWishRepository {
    suspend fun generateImage(prompt: String, model: String): String
    suspend fun generateWishPromptByHolidayName(holidayName: String, model: String? = null, languageCode: String? = "ru"): String
    suspend fun improveWishPrompt(prompt: String, model: String? = null, languageCode: String? = "ru"): String
    fun getImageModels(): Flow<UiState<List<String>>>
    suspend fun sendWish(request: SendWishRequest)
    fun uploadImage(imageUri: Uri): Flow<UiState<String>>
    suspend fun translateTextToEnglish(prompt: String): String
}