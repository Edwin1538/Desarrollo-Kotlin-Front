package com.example.appinterface.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_ROLES= "http://10.0.2.2:8080/"
    private const val BASE_URL_ACUDIENTES= "http://10.0.2.2:8080/"

    val apiRoles: ApiServicesRoles by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ROLES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicesRoles::class.java)
    }

    val apiAcudientes: ApiServicesAcudientes by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL_ACUDIENTES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicesAcudientes::class.java)
    }
}