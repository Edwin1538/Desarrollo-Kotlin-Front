package com.example.appinterface.Api

import retrofit2.Call
import retrofit2.http.GET

interface ApiServicesRoles {
    @GET("/roles")
    fun getRoles(): Call<List<String>>
}