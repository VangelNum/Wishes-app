package com.vangelnum.wisher.features.home.sendwish.stage3.data.model

import com.google.gson.annotations.SerializedName

data class ImgbbResponse(
    @SerializedName("data") val data: ImageData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)