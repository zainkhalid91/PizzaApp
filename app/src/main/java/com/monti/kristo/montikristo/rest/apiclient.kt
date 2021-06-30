package com.monti.kristo.montikristo.rest


import com.monti.kristo.montikristo.rest.network.ApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class apiclient private constructor() {

    val api: ApiInterface
        get() = retrofit!!.create(ApiInterface::class.java)

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_LOCAL)
                .client(OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    companion object {

        val BASE_URL = "https://montikristo.com/api/"
        val BASE_URL_LOCAL = "http://192.168.1.22:3000/api/"

        private var retrofit: Retrofit? = null
        private var apiClientInstance: apiclient? = null

        @Synchronized
        fun getApiClientInstance(): apiclient {

            if (apiClientInstance == null) {
                apiClientInstance = apiclient()
            }

            return apiClientInstance as apiclient
        }
    }
}
