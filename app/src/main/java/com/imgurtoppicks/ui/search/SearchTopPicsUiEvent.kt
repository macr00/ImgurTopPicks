package com.imgurtoppicks.ui.search

sealed class SearchTopPicsUiEvent {
    data class Query(val query: String) : SearchTopPicsUiEvent()
    data class Toggle(val isToggled: Boolean) : SearchTopPicsUiEvent()
}