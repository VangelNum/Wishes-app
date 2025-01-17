package com.vangelnum.wisher.features.home.sendwish.stage3.data.model

import com.google.gson.annotations.SerializedName

data class ImageInfo(
    @SerializedName("filename") val filename: String,
    @SerializedName("name") val name: String,
    @SerializedName("mime") val mime: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("url") val url: String
)