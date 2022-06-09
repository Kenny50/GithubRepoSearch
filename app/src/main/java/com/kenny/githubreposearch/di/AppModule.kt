package com.kenny.githubreposearch.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kenny.githubreposearch.BuildConfig
import com.kenny.githubreposearch.data.remote.GithubServiceApi
import com.kenny.githubreposearch.data.repository.GithubRepositoryImpl
import com.kenny.githubreposearch.domain.repository.GithubRepository
import com.kenny.githubreposearch.util.Constant
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
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                this.addInterceptor(interceptor = loggingInterceptor)
            }
            addInterceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter(Constant.CLIENT_ID, BuildConfig.CLIENT_ID)
                    .addQueryParameter(Constant.CLIENT_SECRET, BuildConfig.CLIENT_SECRET)
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
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