package com.example.appinterface

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.PersonaAdapter
import com.example.appinterface.Modelos.Docente
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocentesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var docentesList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docentes)

        recyclerView = findViewById(R.id.listDocentes)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun volverpag(v: View) {
        onBackPressed()
    }

    fun mostrardocentes(v: View) {
        RetrofitInstance.apiDocentes.getDocentes().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.isNotEmpty()) {
                        docentesList.clear()
                        docentesList.addAll(data)
                        val adapter = PersonaAdapter(docentesList)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@DocentesActivity, "No hay docentes disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DocentesActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@DocentesActivity, "Error en la conexión con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun crearDocente(v: View) {
        // Primero pedir usuario_id
        mostrarDialogoUsuarioId("ID de Usuario para Crear Docente") { usuarioId ->
            // Luego mostrar el diálogo con nombre y apellido
            mostrarDialogoDocente("Crear Docente", null) { nombre, apellido ->
                val docente = Docente(
                    usuario_id = usuarioId,
                    nombre = nombre,
                    apellido = apellido
                )

                RetrofitInstance.apiDocentes.crearDocente(docente).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DocentesActivity, "Docente creado exitosamente", Toast.LENGTH_SHORT).show()
                            mostrardocentes(v)
                        } else {
                            Toast.makeText(this@DocentesActivity, "Error al crear docente", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@DocentesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun editarDocente(v: View) {
        mostrarDialogoID("Editar Docente") { id ->
            mostrarDialogoDocente("Editar Docente", null) { nombre, apellido ->
                val docente = Docente(
                    usuario_id = 1, // Valor temporal - tu backend debería ignorar esto en edición
                    nombre = nombre,
                    apellido = apellido
                )

                RetrofitInstance.apiDocentes.editarDocente(id, docente).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DocentesActivity, "Docente editado exitosamente", Toast.LENGTH_SHORT).show()
                            mostrardocentes(v)
                        } else {
                            Toast.makeText(this@DocentesActivity, "Error al editar docente", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@DocentesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun eliminarDocente(v: View) {
        mostrarDialogoID("Eliminar Docente") { id ->
            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este docente?")
                .setPositiveButton("Eliminar") { _, _ ->
                    RetrofitInstance.apiDocentes.eliminarDocente(id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@DocentesActivity, "Docente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                mostrardocentes(v)
                            } else {
                                Toast.makeText(this@DocentesActivity, "Error al eliminar docente", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@DocentesActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarDialogoDocente(titulo: String, docente: Docente?, callback: (String, String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_docente, null)

        val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
        val editApellido = dialogView.findViewById<EditText>(R.id.editApellido)

        // Si es edición, pre-llenar los campos
        docente?.let {
            editNombre.setText(it.nombre)
            editApellido.setText(it.apellido)
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = editNombre.text.toString().trim()
                val apellido = editApellido.text.toString().trim()

                if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
                    callback(nombre, apellido)
                } else {
                    Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoUsuarioId(titulo: String, callback: (Int) -> Unit) {
        val editText = EditText(this)
        editText.hint = "Ingrese el ID de Usuario"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(editText)
            .setPositiveButton("Continuar") { _, _ ->
                val usuarioIdText = editText.text.toString().trim()
                if (usuarioIdText.isNotEmpty()) {
                    try {
                        val usuarioId = usuarioIdText.toInt()
                        callback(usuarioId)
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "ID de Usuario debe ser un número válido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "ID de Usuario es obligatorio", Toast.LENGTH_SHORT).show()
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