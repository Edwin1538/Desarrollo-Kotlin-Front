package com.example.appinterface.Api

import com.example.appinterface.Modelos.Acudiente
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServicesAcudientes {
    @GET("/acudientes")
    fun getAcudientes(): Call<List<String>>

    @POST("/acudientes")
    fun crearAcudiente(@Body acudiente: Acudiente): Call<Void>

    @PUT("/acudientes/{id}")
    fun editarAcudiente(@Path("id") id: Int, @Body acudiente: Acudiente): Call<Void>

    @DELETE("/acudientes/{id}")
    fun eliminarAcudiente(@Path("id") id: Int): Call<Void>
}