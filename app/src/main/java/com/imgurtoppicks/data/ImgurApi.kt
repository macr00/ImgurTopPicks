package com.imgurtoppicks.data

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

// Client ID = c9e93715d6a2cb7
// Client Secret = 181b375a00af139b6a29c3c47e9388544837416d

const val BASE_URL = "https://api.imgur.com/3/"

interface ImgurApi {

    @GET("gallery/search/top/week")
    fun searchTopPicks(@Query("q") query: String) : Observable<ImgurSearchResponse>
}