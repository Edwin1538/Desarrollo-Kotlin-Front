package com.example.appinterface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Modelos.Docente
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Adaptador mejorado que funciona con tu estructura actual
class DocenteModernoAdapter(
    private var docentes: MutableList<DocenteDisplay>,
    private val onDocenteClick: (DocenteDisplay) -> Unit,
    private val onMenuClick: (DocenteDisplay, View) -> Unit
) : RecyclerView.Adapter<DocenteModernoAdapter.DocenteViewHolder>() {

    // Clase para mostrar datos en la UI
    data class DocenteDisplay(
        val id: String,
        val nombre: String,
        val email: String = "",

    )

    class DocenteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreDocente: TextView = itemView.findViewById(R.id.nombreDocente)
        val materiaDocente: TextView = itemView.findViewById(R.id.materiaDocente)
        val emailDocente: TextView = itemView.findViewById(R.id.emailDocente)
        val menuOptions: ImageButton = itemView.findViewById(R.id.menuOptions)
        val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocenteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_docente, parent, false)
        return DocenteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocenteViewHolder, position: Int) {
        val docente = docentes[position]

        holder.nombreDocente.text = docente.nombre


        // Configurar indicador de estado



        holder.itemView.setOnClickListener { onDocenteClick(docente) }
        holder.menuOptions.setOnClickListener { onMenuClick(docente, it) }
    }

    override fun getItemCount(): Int = docentes.size

    fun updateDocentes(nuevosDocentes: List<DocenteDisplay>) {
        docentes.clear()
        docentes.addAll(nuevosDocentes)
        notifyDataSetChanged()
    }
}

class DocentesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var docenteAdapter: DocenteModernoAdapter
    private var docentesList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docentes)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.listDocentes)

        docenteAdapter = DocenteModernoAdapter(
            docentes = mutableListOf(),
            onDocenteClick = { docente ->
                Toast.makeText(this, "Seleccionado: ${docente.nombre}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { docente, view ->
                showPopupMenu(docente, view)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DocentesActivity)
            adapter = docenteAdapter
        }
    }

    private fun showPopupMenu(docente: DocenteModernoAdapter.DocenteDisplay, view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.docente_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_editar -> {
                    editarDocenteDesdeMenu(docente)
                    true
                }
                R.id.menu_eliminar -> {
                    eliminarDocenteDesdeMenu(docente)
                    true
                }
                else -> false
            }
        }
        popup.show()
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

                        // Convertir List<String> a DocenteDisplay para el adaptador moderno
                        val docentesDisplay = data.mapIndexed { index, docenteString ->
                            // Parseamos el string del docente (asumiendo formato "ID: Nombre Apellido")
                            val partes = docenteString.split(":", limit = 2)
                            val id = if (partes.size > 1) partes[0].trim() else index.toString()
                            val nombreCompleto = if (partes.size > 1) partes[1].trim() else docenteString

                            DocenteModernoAdapter.DocenteDisplay(
                                id = id,
                                nombre = nombreCompleto,)
                        }

                        docenteAdapter.updateDocentes(docentesDisplay)
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
        mostrarDialogoUsuarioId("ID de Usuario para Crear Docente") { usuarioId ->
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
                    usuario_id = 1,
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

    // Métodos para el menú contextual
    private fun editarDocenteDesdeMenu(docente: DocenteModernoAdapter.DocenteDisplay) {
        val id = try { docente.id.toInt() } catch (e: Exception) { 1 }

        mostrarDialogoDocente("Editar Docente", null) { nombre, apellido ->
            val docenteEditado = Docente(
                usuario_id = 1,
                nombre = nombre,
                apellido = apellido
            )

            RetrofitInstance.apiDocentes.editarDocente(id, docenteEditado).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DocentesActivity, "Docente editado exitosamente", Toast.LENGTH_SHORT).show()
                        mostrardocentes(View(this@DocentesActivity))
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

    private fun eliminarDocenteDesdeMenu(docente: DocenteModernoAdapter.DocenteDisplay) {
        val id = try { docente.id.toInt() } catch (e: Exception) { 1 }

        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de eliminar a ${docente.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                RetrofitInstance.apiDocentes.eliminarDocente(id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@DocentesActivity, "Docente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            mostrardocentes(View(this@DocentesActivity))
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

    // MÉTODOS DE DIÁLOGO (SIN DUPLICADOS)
    private fun mostrarDialogoDocente(titulo: String, docente: Docente?, callback: (String, String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_docente, null)

        val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
        val editApellido = dialogView.findViewById<EditText>(R.id.editApellido)

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