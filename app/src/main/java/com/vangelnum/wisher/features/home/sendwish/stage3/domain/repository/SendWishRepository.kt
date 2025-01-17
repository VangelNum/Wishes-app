package com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository

import android.net.Uri
import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.SendWishRequest

interface SendWishRepository {
    suspend fun generateImage(prompt: String, model: String): String
    suspend fun listOfModels(): List<String>
    suspend fun sendWish(request: SendWishRequest)
    suspend fun uploadImage(imageUri: Uri): String
}