package com.kenny.githubreposearch.data.remote.paging_source

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}
