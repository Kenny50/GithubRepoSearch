package com.kenny.githubreposearch.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.data.remote.paging_source.UiAction
import com.kenny.githubreposearch.data.remote.paging_source.UiState
import com.kenny.githubreposearch.domain.use_case.SearchRepositoriesUseCase
import com.kenny.githubreposearch.util.Constant.DEFAULT_QUERY
import com.kenny.githubreposearch.util.paging.PagingStateHelper
import com.kenny.githubreposearch.util.paging.PagingStateManagerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), PagingStateHelper<RepoDateVo> {

    private val pagingStateManager: PagingStateManagerImpl<RepoDateVo>

    init {
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY

        pagingStateManager = PagingStateManagerImpl(
            initialQuery, lastQueryScrolled, viewModelScope
        ) { query ->
            searchRepositoriesUseCase(query)
        }
    }

    override val state: StateFlow<UiState> by lazy {
        pagingStateManager.state
            .onEach {
                savedStateHandle[LAST_SEARCH_QUERY] = it.query
                savedStateHandle[LAST_QUERY_SCROLLED] = it.lastQueryScrolled
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )
    }

    override val pagingDataFlow: Flow<PagingData<RepoDateVo>>
        get() = pagingStateManager.pagingDataFlow

    override val accept: (UiAction) -> Unit
        get() = pagingStateManager.accept
}

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"