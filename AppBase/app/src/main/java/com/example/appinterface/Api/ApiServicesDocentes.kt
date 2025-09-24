package com.example.appinterface.Api

import retrofit2.Call
import retrofit2.http.GET

interface ApiServicesDocentes {
    @GET("/docentes")
    fun getDocentes(): Call<List<String>>
}