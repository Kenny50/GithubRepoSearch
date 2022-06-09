package com.kenny.githubreposearch.data.repository

import com.kenny.githubreposearch.data.local.RepositoriesVo
import com.kenny.githubreposearch.data.remote.GithubServiceApi
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubServiceApi
) : GithubRepository {

    override suspend fun searchRepositories(
        q: String,
        page: Int,
    ): RepositoriesVo {
        return api.searchGithubRepositories(q, page, Constant.DEFAULT_PAGE_SIZE).toRepositoriesVo()
    }
}