package com.imgurtoppicks.ui.search

interface SearchTopPicsView {

    fun displayResults(results: List<SearchGalleryListItem>)

    fun displayLoading(isLoading: Boolean)

    fun displayEmpty(isEmpty: Boolean)

    fun displayNoResults(noResults: Boolean)

    fun displayError(error: Throwable)
}