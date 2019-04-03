package com.monti.kristo.montikristo.rest;


import com.monti.kristo.montikristo.rest.network.ApiInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class apiclient {

    public static final String BASE_URL = "https://montikristo.com/api/";
    public static final String BASE_URL_LOCAL = "http://192.168.1.11:3000/api/";

    private static Retrofit retrofit = null;
    private static apiclient apiClientInstance;

    private apiclient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized apiclient getApiClientInstance() {

        if (apiClientInstance == null) {
            apiClientInstance = new apiclient();
        }

        return apiClientInstance;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}
