package com.kenny.githubreposearch.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kenny.githubreposearch.BuildConfig
import com.kenny.githubreposearch.data.remote.GithubServiceApi
import com.kenny.githubreposearch.data.repository.GithubRepositoryImpl
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant.AUTHORIZATION
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGithubService(): GithubServiceApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    interceptor = HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                )
            }
            addInterceptor { chain ->
                chain.request().newBuilder().addHeader(AUTHORIZATION, BuildConfig.CLIENT_TOKEN)
                    .build().let {
                        chain.proceed(it)
                    }
            }
        }.build()
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
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