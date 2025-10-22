package com.vangelnum.wishes.features.editprofile.data.model

data class EditProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val newPassword: String? = null,
    val currentPassword: String? = null
)