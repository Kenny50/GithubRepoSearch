package com.kenny.githubreposearch.domain.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kenny.githubreposearch.data.remote.paging_source.SearchRepositoriesPagingSource
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant
import javax.inject.Inject

class SearchRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(q: String) = Pager(
        PagingConfig(
            pageSize = Constant.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = 2 * Constant.DEFAULT_PAGE_SIZE
        )
    ) {
        SearchRepositoriesPagingSource(repository, q)
    }.flow
}