package com.kenny.githubreposearch.util.paging

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kenny.githubreposearch.data.remote.paging_source.UiAction
import com.kenny.githubreposearch.data.remote.paging_source.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Abstract helper, define basic usage of paging state
 *
 * @param T
 */
interface PagingStateHelper<T : Any> {
    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<T>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit
}

/**
 * Manager state on paging behavior, using [accept] to trigger refresh, expose data by [pagingDataFlow]
 *
 * @param T
 * @constructor
 *
 *
 * there will be two flow produced via [filterIsInstance] for each data class from same [MutableSharedFlow]
 *
 *
 * @param initialQuery
 * @param lastQueryScrolled
 * @param viewModelScope
 * @param pagingSource
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PagingStateManagerImpl<T : Any>(
    private val initialQuery: String,
    private val lastQueryScrolled: String,
    private val viewModelScope: CoroutineScope,
    pagingSource: (String) -> Flow<PagingData<T>>
) {
    /**
     * Stream of immutable states representative of the UI.
     */
    val state: Flow<UiState>

    val pagingDataFlow: Flow<PagingData<T>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()

        val searches = actionStateFlow.filterActionSearch()
        val queriesScrolled = actionStateFlow.filterActionQueriesAndCache()

        pagingDataFlow = searches
            .filterNot { it.query.isBlank() }
            .flatMapLatest { pagingSource(it.query) }
            .cachedIn(viewModelScope)

        //ui action的兩個flow會造成  ui state的改變，當兩個query一樣時，表示用戶滾動中
        state = buildUiStateStateFlow(searches, queriesScrolled)

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    /**
     * Build [UiState] flow to control ui.
     *
     * combine [UiAction.Search] flow and [UiAction.Scroll] flow to define scroll state
     *
     * @param searches previous search query
     * @param queriesScrolled query while recyclerview is scrolling
     * @return [UiState] to control recyclerview scroll to top while search
     */
    private fun buildUiStateStateFlow(
        searches: Flow<UiAction.Search>,
        queriesScrolled: Flow<UiAction.Scroll>
    ): Flow<UiState> {
        return combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
    }

    /**
     * Add [UiAction.Search] filter and make sure this flow will not collect same value continuous
     * @return Flow<UiAction.Search>
     */
    private fun MutableSharedFlow<UiAction>.filterActionSearch() =
        this
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }


    /**
     * Add [UiAction.Scroll] filter and make sure this flow will not collect same value continuous
     * @return Flow<UiAction.Scroll>
     */
    private fun MutableSharedFlow<UiAction>.filterActionQueriesAndCache() =
        this
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
}