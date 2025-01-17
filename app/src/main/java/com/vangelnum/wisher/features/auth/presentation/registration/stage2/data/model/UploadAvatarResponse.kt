package com.vangelnum.wisher.features.auth.presentation.registration.stage2.data.model

data class UploadAvatarResponse(
    val data: UploadAvatarData?,
    val success: Boolean,
    val status: Int
)
data class UploadAvatarData(
    val id: String,
    val title: String,
    val url_viewer: String,
    val url: String,
    val display_url: String,
    val width: String,
    val height: String,
    val size: String,
    val time: String,
    val expiration: String,
    val image: UploadAvatarInfo,
    val thumb: UploadAvatarInfo,
    val medium: UploadAvatarInfo?,
    val delete_url: String
)

data class UploadAvatarInfo(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)