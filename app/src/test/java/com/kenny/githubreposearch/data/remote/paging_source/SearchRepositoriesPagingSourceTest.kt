package com.kenny.githubreposearch.data.remote.paging_source

import androidx.paging.PagingSource
import com.kenny.githubreposearch.TestRepository
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoriesPagingSourceTest {
    private val repository: GithubRepository = TestRepository()

    @Test
    fun `load return page on success as first call`() = runTest {
        val testPageSize = Constant.DEFAULT_PAGE_SIZE
        val data = listOf(
            repository.searchRepositories("", 1, testPageSize),
            repository.searchRepositories("", 2, testPageSize),
        )

        val pagingSource = SearchRepositoriesPagingSource(repository, "")

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = data[0].repositories + data[1].repositories,
                prevKey = null,
                nextKey = 3
            ),
            //our first call is (2 * DEFAULT_PAGE_SIZE) define in PagingConfig, therefore times 2 here as well
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = testPageSize * 2,
                    placeholdersEnabled = false
                )
            ) as PagingSource.LoadResult.Page,
        )
    }

    @Test
    fun `load return page on success as regular call`() = runTest {
        val testPageSize = Constant.DEFAULT_PAGE_SIZE
        val currentPage = 10

        val data = listOf(
            repository.searchRepositories("", currentPage, testPageSize),
        )

        val pagingSource = SearchRepositoriesPagingSource(repository, "")

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = data[0].repositories,
                prevKey = currentPage - 1,
                nextKey = currentPage + 1
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = currentPage,
                    loadSize = testPageSize,
                    placeholdersEnabled = false
                )
            ) as PagingSource.LoadResult.Page,
        )
    }

    @Test
    fun `load return page on success as finish call`() = runTest {
        val testPageSize = Constant.DEFAULT_PAGE_SIZE
        val currentPage = 20

        val data = listOf(
            repository.searchRepositories("", currentPage, testPageSize),
        )

        val pagingSource = SearchRepositoriesPagingSource(repository, "")

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = data[0].repositories,
                prevKey = currentPage - 1,
                nextKey = currentPage + 1
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = currentPage,
                    loadSize = testPageSize,
                    placeholdersEnabled = false
                )
            ) as PagingSource.LoadResult.Page,
        )
    }
}