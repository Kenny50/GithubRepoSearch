package com.kenny.githubreposearch

import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.data.local.RepositoriesVo
import com.kenny.githubreposearch.domain.repository.GithubRepository

class TestRepository : GithubRepository {
    override suspend fun searchRepositories(q: String, page: Int, perPage: Int): RepositoriesVo {

        val expectStart = (page - 1) * perPage
        val expectEnd = expectStart + perPage
        return when {
            expectStart > fakeRepositories.size -> {
                RepositoriesVo(
                    incompleteResults = true,
                    repositories = emptyList(),
                    totalCount = fakeRepositories.size
                )
            }
            fakeRepositories.size in (expectStart + 1) until expectEnd -> {
                RepositoriesVo(
                    incompleteResults = true,
                    repositories = fakeRepositories.subList(expectStart, fakeRepositories.size),
                    totalCount = fakeRepositories.size
                )
            }
            expectEnd < fakeRepositories.size -> {
                RepositoriesVo(
                    incompleteResults = false,
                    repositories = fakeRepositories.subList(expectStart, expectEnd),
                    totalCount = fakeRepositories.size
                )
            }
            else -> throw Exception("Uncheck exception, with page $page, perpage $perPage, start $expectStart, end $expectEnd")
        }
    }

    private companion object {
        val fakeRepositories = (1..995).map {
            RepoDateVo(
                repositoryName = it.toString(),
                startCount = it,
                forkCount = it,
                description = it.toString(),
                license = null,
                tags = null,
                authorName = it.toString(),
                authorAvatar = it.toString(),
                id = it
            )
        }
    }
}