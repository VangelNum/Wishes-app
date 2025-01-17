package com.vangelnum.wisher.features.home.sendwish.stage3.data.model

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url_viewer") val urlViewer: String,
    @SerializedName("url") val url: String,
    @SerializedName("display_url") val displayUrl: String,
    @SerializedName("width") val width: String,
    @SerializedName("height") val height: String,
    @SerializedName("size") val size: String,
    @SerializedName("time") val time: String,
    @SerializedName("expiration") val expiration: String,
    @SerializedName("image") val image: ImageInfo,
    @SerializedName("thumb") val thumb: ImageInfo,
    @SerializedName("medium") val medium: ImageInfo?, // Making this nullable as it might not always be present
    @SerializedName("delete_url") val deleteUrl: String
)