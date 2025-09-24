package com.example.appinterface

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.PersonaAdapter
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class
RolesActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_roles)
        }

    fun volverpag(v: View) {
        onBackPressed()
    }

    fun mostrarRoles(v: View) {
        val recyclerView = findViewById<RecyclerView>(R.id.listRoles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        RetrofitInstance.apiRoles.getRoles().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.isNotEmpty()) {
                        val adapter = PersonaAdapter(data)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@RolesActivity, "No hay personas disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RolesActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@RolesActivity, "Error en la conexión con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

}