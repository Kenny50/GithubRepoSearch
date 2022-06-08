package com.kenny.githubreposearch.domain.use_case

import com.kenny.githubreposearch.domain.repository.GithubRepository
import javax.inject.Inject

class SearchRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(q: String) = repository.searchRepositories(q, 2, 10)
}