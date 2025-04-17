package com.vangelnum.wishes.features.home.sendwish.createwish.presentation

import android.net.Uri

sealed class SendWishEvent {
    data class OnGenerateImage(val prompt: String, val model: String) : SendWishEvent()
    data class OnGenerateWishPromptByHoliday(
        val holiday: String,
        val model: String? = null,
        val languageCode: String
    ) : SendWishEvent()

    data class OnImproveWishPrompt(
        val prompt: String,
        val model: String? = null,
        val languageCode: String
    ) : SendWishEvent()

    data object OnGetNumberOfWishes: SendWishEvent()

    data object OnGetImageModels : SendWishEvent()

    data class OnSendWish(
        val text: String,
        val wishDate: String,
        val openDate: String,
        val image: String,
        val maxViewers: Int?,
        val isBlurred: Boolean,
        val cost: Int
    ) : SendWishEvent()

    data class OnUploadImage(val imageUri: Uri) : SendWishEvent()
    data object OnSendBackState : SendWishEvent()
}
