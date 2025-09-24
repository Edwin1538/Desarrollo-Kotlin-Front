package com.example.appinterface

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.*


class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val buttonDocentes: Button = findViewById(R.id.buttonDocentes)
        buttonDocentes.setOnClickListener {
            val intent = Intent(this, DocentesActivity::class.java)
            startActivity(intent)
        }

        val buttonAcudientes: Button = findViewById(R.id.buttonAcudientes)
        buttonAcudientes.setOnClickListener {
            val intent = Intent(this, AcudientesActivity::class.java)
            startActivity(intent)
        }
    }
}

