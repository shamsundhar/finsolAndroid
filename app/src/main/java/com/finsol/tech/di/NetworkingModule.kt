package com.jukti.clearscoredemo.di

import com.finsol.tech.api.MarketDataApiService
import com.finsol.tech.api.InternetConnectionInterceptor
import com.finsol.tech.api.LoginResponseApiService
import com.finsol.tech.api.ProfileResponseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {


    @Provides
    fun providesBaseUrl(): String {
        return "http://35.179.51.36:8001/"
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,
                            internetConnectionInterceptor: InternetConnectionInterceptor
    ): OkHttpClient {


        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.callTimeout(40, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(40, TimeUnit.SECONDS)
        okHttpClient.readTimeout(40, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(40, TimeUnit.SECONDS)
        okHttpClient.addInterceptor(internetConnectionInterceptor)
        okHttpClient.addInterceptor(loggingInterceptor)
        okHttpClient.build()
        return okHttpClient.build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

//    @Provides
//    fun provideRestApiService(retrofit: Retrofit): MarketDataApiService {
//        return retrofit.create(MarketDataApiService::class.java)
//    }

    @Provides
    fun provideRestApiService(retrofit: Retrofit): LoginResponseApiService {
        return retrofit.create(LoginResponseApiService::class.java)
    }
    @Provides
    fun provideProfileApiService(retrofit: Retrofit): ProfileResponseApiService {
        return retrofit.create(ProfileResponseApiService::class.java)
    }

}