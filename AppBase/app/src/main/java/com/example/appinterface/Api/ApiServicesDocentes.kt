package com.example.appinterface.Api

import com.example.appinterface.Modelos.Docente
import retrofit2.Call
import retrofit2.http.*

interface ApiServicesDocentes {
    @GET("/docentes")
    fun getDocentes(): Call<List<String>>

    @POST("/docentes")
    fun crearDocente(@Body docente: Docente): Call<Void>

    @PUT("/docentes/{id}")
    fun editarDocente(@Path("id") id: Int, @Body docente: Docente): Call<Void>

    @DELETE("/docentes/{id}")
    fun eliminarDocente(@Path("id") id: Int): Call<Void>
}