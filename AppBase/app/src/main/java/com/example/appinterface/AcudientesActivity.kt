package com.example.appinterface

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.PersonaAdapter
import com.example.appinterface.Modelos.Acudiente
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcudientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var acudientesList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acudientes)

        recyclerView = findViewById(R.id.listAcudientes)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun volverpag(v: View) {
        onBackPressed()
    }

    fun mostraracudientes(v: View) {
        RetrofitInstance.apiAcudientes.getAcudientes().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.isNotEmpty()) {
                        acudientesList.clear()
                        acudientesList.addAll(data)
                        val adapter = PersonaAdapter(acudientesList)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@AcudientesActivity, "No hay acudientes disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AcudientesActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@AcudientesActivity, "Error en la conexión con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun crearAcudiente(v: View) {
        mostrarDialogoAcudiente("Crear Acudiente", null) { acudiente ->
            RetrofitInstance.apiAcudientes.crearAcudiente(acudiente).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AcudientesActivity, "Acudiente creado exitosamente", Toast.LENGTH_SHORT).show()
                        mostraracudientes(v) // Actualizar lista
                    } else {
                        Toast.makeText(this@AcudientesActivity, "Error al crear acudiente", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AcudientesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun editarAcudiente(v: View) {
        // Mostrar diálogo para seleccionar ID
        mostrarDialogoID("Editar Acudiente") { id ->
            mostrarDialogoAcudiente("Editar Acudiente", null) { acudiente ->
                RetrofitInstance.apiAcudientes.editarAcudiente(id, acudiente).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AcudientesActivity, "Acudiente editado exitosamente", Toast.LENGTH_SHORT).show()
                            mostraracudientes(v) // Actualizar lista
                        } else {
                            Toast.makeText(this@AcudientesActivity, "Error al editar acudiente", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AcudientesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun eliminarAcudiente(v: View) {
        mostrarDialogoID("Eliminar Acudiente") { id ->
            // Confirmar eliminación
            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este acudiente?")
                .setPositiveButton("Eliminar") { _, _ ->
                    RetrofitInstance.apiAcudientes.eliminarAcudiente(id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AcudientesActivity, "Acudiente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                mostraracudientes(v) // Actualizar lista
                            } else {
                                Toast.makeText(this@AcudientesActivity, "Error al eliminar acudiente", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@AcudientesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarDialogoAcudiente(titulo: String, acudiente: Acudiente?, callback: (Acudiente) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_acudiente, null)

        val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
        val editApellido = dialogView.findViewById<EditText>(R.id.editApellido)
        val editCorreo = dialogView.findViewById<EditText>(R.id.editCorreo)

        // Si es edición, pre-llenar los campos
        acudiente?.let {
            editNombre.setText(it.nombre)
            editApellido.setText(it.apellido)
            editCorreo.setText(it.correo)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = editNombre.text.toString().trim()
                val apellido = editApellido.text.toString().trim()
                val correo = editCorreo.text.toString().trim()

                if (nombre.isNotEmpty() && apellido.isNotEmpty() && correo.isNotEmpty()) {
                    callback(Acudiente(nombre = nombre, apellido = apellido, correo = correo))
                } else {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoID(titulo: String, callback: (Int) -> Unit) {
        val editText = EditText(this)
        editText.hint = "Ingrese el ID"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(editText)
            .setPositiveButton("Continuar") { _, _ ->
                val idText = editText.text.toString().trim()
                if (idText.isNotEmpty()) {
                    try {
                        val id = idText.toInt()
                        callback(id)
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "ID debe ser un número válido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "ID es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}