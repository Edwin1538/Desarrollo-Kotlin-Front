package com.example.appinterface.Api

import retrofit2.Call
import retrofit2.http.GET

interface ApiServicesAcudientes {
    @GET("/acudientes")
    fun getAcudientes(): Call<List<String>>
}