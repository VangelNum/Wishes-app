package com.vangelnum.wisher.features.auth.presentation.registration.stage1

import android.content.Context
import android.net.Uri
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest

sealed class RegistrationEvent {
    data class OnRegisterUser(val user: RegistrationRequest): RegistrationEvent()
    data object OnBackToEmptyState: RegistrationEvent()
    data class OnUploadAvatar(val context: Context, val imageUri: Uri): RegistrationEvent()
}