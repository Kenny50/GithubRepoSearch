package com.kenny.githubreposearch.domain.repository

import com.kenny.githubreposearch.data.local.RepositoriesVo

interface GithubRepository {

    suspend fun searchRepositories(
        q: String,
        page: Int,
    ): RepositoriesVo

}