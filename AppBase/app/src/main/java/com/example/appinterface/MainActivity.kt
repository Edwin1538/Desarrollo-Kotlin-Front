package com.example.appinterface

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun mostrarDocentes(v: View) {
        val intent = Intent(this, DocentesActivity::class.java)
        startActivity(intent)
    }

    fun mostrarAcudientes(v: View) {
        val intent = Intent(this, AcudientesActivity::class.java)
        startActivity(intent)
    }
}