package com.boilerplate.network.apiclient

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.thdev.network.flowcalladapterfactory.FlowCallAdapterFactory

object NetworkModule {

    @Volatile
    private var instance: NetworkModule? = null

    fun getInstance(): NetworkModule {
        return instance ?: synchronized(this) {
            instance ?: throw IllegalStateException("NetworkModule not initialized")
        }
    }

    private lateinit var retrofit: Retrofit

    fun initialize(baseUrl: String, headers: Map<String, String>) {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                headers.forEach { (key, value) ->
                    request.addHeader(key, value)
                }
                chain.proceed(request.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .build()

        instance = this
    }

    fun <T> createApi(api: Class<T>): T {
        return retrofit.create(api)
    }
}