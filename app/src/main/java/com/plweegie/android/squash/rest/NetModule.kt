package com.plweegie.android.squash.rest


import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.plweegie.android.squash.utils.QueryPreferences
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

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
    fun provideAuthorizationInterceptor(preferences: QueryPreferences): Interceptor = Interceptor { chain ->
        var request = chain.request()
        val headers = request.headers().newBuilder()
                .add("Authorization", "token ${preferences.storedAccessToken}")
                .build()

        request = request.newBuilder().headers(headers).build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
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
