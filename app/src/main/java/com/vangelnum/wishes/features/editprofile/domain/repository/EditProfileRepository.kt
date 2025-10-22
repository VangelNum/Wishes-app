package com.vangelnum.wishes.features.editprofile.domain.repository

import android.content.Context
import android.net.Uri
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface EditProfileRepository {
    fun updateUserProfile(
        name: String?,
        email: String?,
        avatarUrl: String?,
        newPassword: String?,
        currentPassword: String?,
    ): Flow<UiState<AuthResponse>>

    fun uploadProfileImage(imageUri: Uri, context: Context): Flow<UiState<String>>
}