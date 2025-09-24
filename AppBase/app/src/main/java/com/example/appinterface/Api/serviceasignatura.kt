package com.example.appinterface.Api

import retrofit2.Call
import retrofit2.http.GET

interface serviceasignatura {
    @GET("/asignaturas")
    fun getPersonas(): Call<List<String>>
}