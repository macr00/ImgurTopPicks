package com.imgurtoppicks.ui.search

import android.content.Context
import com.imgurtoppicks.R
import com.imgurtoppicks.data.ImgurGallery
import java.text.SimpleDateFormat
import java.util.*

data class SearchGalleryListItem(val gallery: ImgurGallery) {

    val id = gallery.id
    val title = gallery.title

    fun getCoverImageUrl(): String? {
        with (gallery) {
            return if (images == null) {
                null
            } else {
                images[0].link
            }
        }
    }

    fun getDateString(dateFormat: SimpleDateFormat): String {
        return dateFormat.format(Date(gallery.datetime * 1000L))
    }

    fun getImageCount(context: Context): String {
        val count = gallery.imageCount ?: 0
        return context.resources.getQuantityString(R.plurals.number_of_images, count, count)
    }
}