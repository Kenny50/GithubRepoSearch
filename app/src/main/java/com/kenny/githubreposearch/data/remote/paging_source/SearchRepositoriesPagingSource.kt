package com.kenny.githubreposearch.data.remote.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant
import com.kenny.githubreposearch.util.Constant.DEFAULT_FIRST_PAGE_INDEX
import com.kenny.githubreposearch.util.Constant.RATE_LIMIT_CODE_FOR_PUBLIC_READ_REQUEST
import com.kenny.githubreposearch.util.Constant.RATE_LIMIT_MESSAGE
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/**
 * Generate a Paging source for each github repositories search.
 *
 * @property repository provide the api request method
 * @property query the keyword pass to api request
 */
class SearchRepositoriesPagingSource(
    private val repository: GithubRepository,
    private val query: String
) : PagingSource<Int, RepoDateVo>() {

    /**
     * Load api request.
     *
     * According to github document, the max amount of repositories is 100 on each request,
     * therefore the first request will ask for 100 data, after that, each request will call for 50
     *
     * @param params default params
     * @return LoadResult.Page while success else LoadResult.Error
     */
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, RepoDateVo> {
        return try {
            // Start refresh at page 1 if undefined.
            val currentPage = params.key ?: DEFAULT_FIRST_PAGE_INDEX
            Timber.d(params.loadSize.toString())
            val response = repository.searchRepositories(query, currentPage, params.loadSize)
            LoadResult.Page(
                data = response.repositories,
                prevKey = if (currentPage == DEFAULT_FIRST_PAGE_INDEX) null else currentPage - 1, // Only paging forward.
                nextKey = if (response.repositories.isEmpty()) null else currentPage + (params.loadSize / Constant.DEFAULT_PAGE_SIZE)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return if (
                exception.code() == RATE_LIMIT_CODE_FOR_PUBLIC_READ_REQUEST
            ) LoadResult.Error(Exception(RATE_LIMIT_MESSAGE))
            else LoadResult.Error(exception)
        }
    }

    /**
     * Get the right key while recyclerview scroll.
     *
     * @param state default params
     * @return key
     */
    override fun getRefreshKey(state: PagingState<Int, RepoDateVo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}