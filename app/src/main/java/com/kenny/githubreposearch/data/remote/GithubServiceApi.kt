package com.kenny.githubreposearch.data.remote

import com.kenny.githubreposearch.data.remote.dto.RepositoriesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubServiceApi {

    @GET("search/repositories")
    suspend fun searchGithubRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): RepositoriesDto
}