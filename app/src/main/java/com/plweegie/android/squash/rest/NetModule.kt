package com.plweegie.android.squash.rest


import android.app.Application

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetModule(private val baseUrl: String) {

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        val cacheSize = 5 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): GitHubService =
        retrofit.create(GitHubService::class.java)
}
