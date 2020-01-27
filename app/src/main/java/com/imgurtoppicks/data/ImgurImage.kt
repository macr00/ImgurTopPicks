package com.imgurtoppicks.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurImage(
    val id: String,
    val link: String
)