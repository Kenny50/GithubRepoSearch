package com.kenny.githubreposearch.data.remote.paging_source

import com.kenny.githubreposearch.util.Constant.DEFAULT_QUERY

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)
