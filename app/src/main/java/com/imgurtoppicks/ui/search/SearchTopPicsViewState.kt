package com.imgurtoppicks.ui.search

import android.util.Log
import com.imgurtoppicks.data.ImgurGallery
import com.imgurtoppicks.domain.search.SearchTopPicsStatus

data class SearchTopPicsViewState(
    private val galleries: List<ImgurGallery> = emptyList(),
    private val isLoading: Boolean = false,
    private val isToggled: Boolean = false,
    private val error: Throwable? = null,
    val query: String = ""
) {

    fun renderWith(view: SearchTopPicsView) {
        Log.d("ViewState", "State $this.toString()")
        Log.d("ViewState", "Render")

        view.run {
            displayEmpty(false)
            displayResults(getListItems())
            displayLoading(isLoading)

            if (error != null) {
                displayError(error)
            }

            if (query.isNotEmpty() && galleries.isEmpty()) {
                displayNoResults(true)
            } else {
                displayNoResults(false)
            }
        }
    }

    private fun getListItems(): List<SearchGalleryListItem> {
        return if (!isToggled) {
            galleries.map { SearchGalleryListItem(it) }
        } else {
            galleries
                .filter { (it.points + it.score + it.topicId) % 2 == 0 }
                .map { SearchGalleryListItem(it) }
        }
    }
}

typealias StateReducer<S> = (S) -> S

fun reduceWith(status: SearchTopPicsStatus): StateReducer<SearchTopPicsViewState> = { state ->
    when (status) {
        is SearchTopPicsStatus.Loading -> state.copy(
            isLoading = true, error = null, query = status.query
        )
        is SearchTopPicsStatus.Error -> state.copy(
            isLoading = false, error = status.throwable, query = status.query
        )
        is SearchTopPicsStatus.Success -> state.copy(
            isLoading = false, error = null, query = status.query, galleries = status.searchResponse.data
        )
    }
}

fun reduceWith(isToggled: Boolean): StateReducer<SearchTopPicsViewState> = { state ->
    state.copy(isToggled = isToggled)
}

