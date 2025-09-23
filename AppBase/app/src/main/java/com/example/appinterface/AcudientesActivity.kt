package com.example.appinterface

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.AcudienteAdapter
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcudientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acudientes)

        recyclerView = findViewById(R.id.recyclerViewAcudientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarAcudientes()
    }

    private fun cargarAcudientes() {
        RetrofitInstance.api2kotlin.getAcudientes().enqueue(object : Callback<List<Acudiente>> {
            override fun onResponse(call: Call<List<Acudiente>>, response: Response<List<Acudiente>>) {
                if (response.isSuccessful) {
                    response.body()?.let { acudientes ->
                        recyclerView.adapter = AcudienteAdapter(acudientes)
                    }
                }
            }

            override fun onFailure(call: Call<List<Acudiente>>, t: Throwable) {
                // Manejar error - podrías mostrar un Toast o Log
            }
        })
    }

    fun volverpag(v: View) {
        onBackPressed()
    }
}