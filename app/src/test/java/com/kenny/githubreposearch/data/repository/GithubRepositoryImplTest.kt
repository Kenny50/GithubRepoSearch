package com.kenny.githubreposearch.data.repository

import com.google.gson.GsonBuilder
import com.kenny.githubreposearch.data.local.RepoDateVo
import com.kenny.githubreposearch.data.local.RepositoriesVo
import com.kenny.githubreposearch.data.remote.GithubServiceApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GithubRepositoryImplTest {

    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(GithubServiceApi::class.java)

    private val repositoryImpl = GithubRepositoryImpl(api)


    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `request github repository data and get 200 code and data`() {
        mockWebServer.enqueueResponse("github_repo_paging_200.json", 200)

        runBlocking {
            val actual = repositoryImpl.searchRepositories("", 1, 1)

            val fakeList = listOf(
                RepoDateVo(
                    repositoryName = "CleanArchitecture",
                    startCount = 10461,
                    forkCount = 1913,
                    description = "Clean Architecture Solution Template: A starting point for Clean Architecture with ASP.NET Core",
                    license = null,
                    tags = null,
                    authorAvatar = "https://avatars.githubusercontent.com/u/782127?v=4",
                    authorName = "ardalis",
                    id = 69673033
                )
            )

            val expected = RepositoriesVo(
                incompleteResults = false,
                fakeList,
                totalCount = 181510
            )

            assertEquals(expected, actual)
        }
    }
}