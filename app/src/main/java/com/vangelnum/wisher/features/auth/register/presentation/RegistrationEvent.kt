package com.vangelnum.wisher.features.auth.register.presentation

import android.content.Context
import android.net.Uri
import com.vangelnum.wisher.features.auth.register.data.model.RegistrationRequest

sealed class RegistrationEvent {
    data class OnRegisterUser(val user: RegistrationRequest) : RegistrationEvent()
    data class OnVerifyEmail(val email: String, val verificationCode: String) : RegistrationEvent()
    data object OnBackToEmptyState : RegistrationEvent()
    data class OnUploadAvatar(val context: Context, val imageUri: Uri) : RegistrationEvent()
    data class OnUpdateAvatar(val imageUri: String) : RegistrationEvent()
}