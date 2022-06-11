package com.kenny.githubreposearch.data.remote.paging_source

import androidx.annotation.Keep

/**
 * Ui action to trigger viewModel callback
 */
@Keep
sealed class UiAction {

    /**
     * Action to search
     *
     * while this action send to shareFlow via callback, the consumer of that flow will trigger search request
     * @property query
     */
    @Keep
    data class Search(val query: String) : UiAction()

    /**
     * Action on scroll
     *
     * while this action send to shareFlow via callback, the consumer of that flow will produce scroll
     * @property currentQuery
     */
    @Keep
    data class Scroll(val currentQuery: String) : UiAction()
}
