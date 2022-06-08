package com.kenny.githubreposearch.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kenny.githubreposearch.BuildConfig
import com.kenny.githubreposearch.data.remote.GithubServiceApi
import com.kenny.githubreposearch.data.repository.GithubRepositoryImpl
import com.kenny.githubreposearch.domain.repository.GithubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGithubService(): GithubServiceApi {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GithubServiceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGithubRepository(api: GithubServiceApi): GithubRepository {
        return GithubRepositoryImpl(api)
    }
}