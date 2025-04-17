package com.vangelnum.wishes.features.profile.domain.repository

import android.content.Context
import android.net.Uri
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
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