package com.imgurtoppicks.domain.search

import android.util.Log
import com.imgurtoppicks.data.ImgurApi
import com.imgurtoppicks.data.ImgurSearchResponse
import com.imgurtoppicks.domain.BaseUseCase
import com.imgurtoppicks.domain.RxSchedulers
import io.reactivex.Observable
import io.reactivex.functions.Function
import javax.inject.Inject

class SearchTopPicsUseCase
@Inject constructor(
    private val api: ImgurApi,
    schedulers: RxSchedulers
) : BaseUseCase<SearchParams, SearchTopPicsStatus>(schedulers) {

    override fun execute(params: SearchParams): Observable<SearchTopPicsStatus> {
        return api.searchTopPicks(params.query)
            .subscribeOn(schedulers.io)
            .map(SearchTopPicsSortFunction(params.query))
            .onErrorReturn { t -> SearchTopPicsStatus.Error(params.query, t) }
            .startWith(SearchTopPicsStatus.Loading(params.query))
    }
}

class SearchTopPicsSortFunction(
    private val query: String
) : Function<ImgurSearchResponse, SearchTopPicsStatus> {

    override fun apply(response: ImgurSearchResponse): SearchTopPicsStatus {
        val sorted = response.data.sortedByDescending { it.datetime }
        return SearchTopPicsStatus.Success(
            query = query,
            searchResponse = response.copy(data = sorted)
        )
    }
}