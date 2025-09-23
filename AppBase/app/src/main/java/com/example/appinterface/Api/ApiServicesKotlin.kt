package com.example.appinterface.Api

import com.example.appinterface.Docente
import com.example.appinterface.Acudiente
import retrofit2.Call
import retrofit2.http.GET

interface ApiServicesKotlin {
    @GET("/docentes")
    fun getDocentes(): Call<List<Docente>>

    @GET("/acudientes")
    fun getAcudientes(): Call<List<Acudiente>>
}