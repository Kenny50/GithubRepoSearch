package com.kenny.githubreposearch.data.remote.paging_source

import androidx.annotation.Keep
import com.kenny.githubreposearch.util.Constant.DEFAULT_QUERY

/**
 * Track user behavior by compare [UiAction.Search.query] and [UiAction.Scroll.currentQuery] and record result
 *
 * @property query query text for search api
 * @property lastQueryScrolled query text for current scrolling recyclerview result
 * @property hasNotScrolledForCurrentSearch determine whether the view capable to scroll to top since refresh
 */
@Keep
data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)
