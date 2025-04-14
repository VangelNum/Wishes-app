package com.vangelnum.wisher.features.profile.domain.repository

import android.content.Context
import android.net.Uri
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface UpdateProfileRepository {
    fun updateUserProfile(
        name: String?,
        email: String?,
        password: String?,
        currentPassword: String?,
        avatar: String?,
    ): Flow<UiState<AuthResponse>>

    fun uploadProfileImage(imageUri: Uri, context: Context): Flow<UiState<String>>
}