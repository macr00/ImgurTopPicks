package com.imgurtoppicks.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurGallery(
    val id: String,
    val title: String,
    val datetime: Long,
    val points: Int,
    val score: Int,
    val images: List<ImgurImage>?,
    @Json(name = "topic_id") val topicId: Int,
    @Json(name = "images_count") val imageCount: Int?
)