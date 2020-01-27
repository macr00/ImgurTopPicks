package com.imgurtoppicks.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurSearchResponse(
    val data: List<ImgurGallery>,
    val status: Int
)