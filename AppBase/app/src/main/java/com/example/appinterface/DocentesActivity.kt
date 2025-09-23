package com.example.appinterface

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.DocenteAdapter
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocentesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docentes)

        recyclerView = findViewById(R.id.recyclerViewDocentes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarDocentes()
    }

    private fun cargarDocentes() {
        RetrofitInstance.api2kotlin.getDocentes().enqueue(object : Callback<List<Docente>> {
            override fun onResponse(call: Call<List<Docente>>, response: Response<List<Docente>>) {
                if (response.isSuccessful) {
                    response.body()?.let { docentes ->
                        recyclerView.adapter = DocenteAdapter(docentes)
                    }
                }
            }

            override fun onFailure(call: Call<List<Docente>>, t: Throwable) {
                // Manejar error
            }
        })
    }

    fun volverpag(v: View) {
        onBackPressed()
    }
}