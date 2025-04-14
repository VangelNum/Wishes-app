package com.vangelnum.wisher.features.home.sendwish.createwish.domain.repository

import android.net.Uri
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.sendwish.createwish.data.model.SendWishRequest
import kotlinx.coroutines.flow.Flow

interface SendWishRepository {
    suspend fun generateImage(prompt: String, model: String): String
    suspend fun generateWishPromptByHolidayName(holidayName: String, model: String? = null, languageCode: String): String
    suspend fun improveWishPrompt(prompt: String, model: String? = null, languageCode: String): String
    fun getImageModels(): Flow<UiState<List<String>>>
    fun sendWish(request: SendWishRequest): Flow<UiState<Wish>>
    fun uploadImage(imageUri: Uri): Flow<UiState<String>>
    suspend fun translateTextToEnglish(prompt: String): String

    suspend fun getNumberWishesOfCurrentUser(): Long
}