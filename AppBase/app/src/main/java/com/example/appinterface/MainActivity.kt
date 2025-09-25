package com.example.appinterface

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun irAcudientes(v: View) {
        val intent = Intent(this, AcudientesActivity::class.java)
        startActivity(intent)
    }

    fun irDocentes(v: View) {
        val intent = Intent(this, DocentesActivity::class.java)
        startActivity(intent)
    }
}