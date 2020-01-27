package com.imgurtoppicks.domain.search

import com.imgurtoppicks.data.ImgurSearchResponse

sealed class SearchTopPicsStatus {

    abstract val query: String

    data class Loading(
        override val query: String
    ) : SearchTopPicsStatus()

    data class Error(
        override val query: String,
        val throwable: Throwable
    ) : SearchTopPicsStatus()

    data class Success(
        override val query: String,
        val searchResponse: ImgurSearchResponse
    ) : SearchTopPicsStatus()
}