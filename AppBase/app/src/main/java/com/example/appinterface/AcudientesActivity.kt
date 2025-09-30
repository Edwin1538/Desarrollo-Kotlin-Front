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
import com.example.appinterface.Modelos.Acudiente
import com.example.appinterface.Api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcudienteModernoAdapter(
    private var acudientes: MutableList<AcudienteDisplay>,
    private val onAcudienteClick: (AcudienteDisplay) -> Unit,
    private val onMenuClick: (AcudienteDisplay, View) -> Unit
) : RecyclerView.Adapter<AcudienteModernoAdapter.AcudienteViewHolder>() {

    // Clase para mostrar datos en la UI
    data class AcudienteDisplay(
        val id: String,
        val nombre: String,
        val apellido: String,
        val correo: String = "",
    )

    class AcudienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreAcudiente: TextView = itemView.findViewById(R.id.nombreAcudiente)
        val apellidoAcudiente: TextView = itemView.findViewById(R.id.apellidoAcudiente)
        val correoAcudiente: TextView = itemView.findViewById(R.id.emailAcudiente)
        val menuOptions: ImageButton = itemView.findViewById(R.id.menuOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcudienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_acudiente, parent, false)
        return AcudienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcudienteViewHolder, position: Int) {
        val acudiente = acudientes[position]

        holder.nombreAcudiente.text = acudiente.nombre
        holder.apellidoAcudiente.text = acudiente.apellido
        holder.correoAcudiente.text = acudiente.correo

        holder.itemView.setOnClickListener { onAcudienteClick(acudiente) }
        holder.menuOptions.setOnClickListener { onMenuClick(acudiente, it) }
    }

    override fun getItemCount(): Int = acudientes.size

    fun updateAcudientes(nuevosAcudientes: List<AcudienteDisplay>) {
        acudientes.clear()
        acudientes.addAll(nuevosAcudientes)
        notifyDataSetChanged()
    }
}

class AcudientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var acudienteAdapter: AcudienteModernoAdapter
    private var acudientesList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acudientes)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.listAcudientes)

        acudienteAdapter = AcudienteModernoAdapter(
            acudientes = mutableListOf(),
            onAcudienteClick = { acudiente ->
                Toast.makeText(this, "Seleccionado: ${acudiente.nombre}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { acudiente, view ->
                showPopupMenu(acudiente, view)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AcudientesActivity)
            adapter = acudienteAdapter
        }
    }

    private fun showPopupMenu(acudiente: AcudienteModernoAdapter.AcudienteDisplay, view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.acudiente_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_editar -> {
                    editarAcudienteDesdeMenu(acudiente)
                    true
                }
                R.id.menu_eliminar -> {
                    eliminarAcudienteDesdeMenu(acudiente)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun editarAcudienteDesdeMenu(acudiente: AcudienteModernoAdapter.AcudienteDisplay) {
        val id = try { acudiente.id.toInt() } catch (e: Exception) { 1 }

        mostrarDialogoAcudiente("Editar Acudiente", null) { nombre, apellido, correo ->
            val acudienteEditado = Acudiente(
                nombre = nombre,
                apellido = apellido,
                correo = correo
            )

            RetrofitInstance.apiAcudientes.editarAcudiente(id, acudienteEditado).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AcudientesActivity, "Acudiente editado exitosamente", Toast.LENGTH_SHORT).show()
                        mostraracudientes(View(this@AcudientesActivity))
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

    private fun eliminarAcudienteDesdeMenu(acudiente: AcudienteModernoAdapter.AcudienteDisplay) {
        val id = try { acudiente.id.toInt() } catch (e: Exception) { 1 }

        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de eliminar a ${acudiente.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                RetrofitInstance.apiAcudientes.eliminarAcudiente(id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AcudientesActivity, "Acudiente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            mostraracudientes(View(this@AcudientesActivity))
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

                        // Convertir List<String> a AcudienteDisplay para el adaptador moderno
                        val acudientesDisplay = data.mapIndexed { index, acudienteString ->
                            // Parseamos el string del acudiente
                            // Ajusta este parsing según el formato que devuelve tu API
                            // Ejemplo: "1: Juan Pérez - juan@email.com"
                            val partes = acudienteString.split(":", limit = 2)
                            val id = if (partes.size > 1) partes[0].trim() else index.toString()
                            val resto = if (partes.size > 1) partes[1].trim() else acudienteString

                            // Si tu formato incluye más información, ajusta aquí
                            val partesNombre = resto.split("-")
                            val nombreCompleto = if (partesNombre.isNotEmpty()) partesNombre[0].trim() else resto
                            val correo = if (partesNombre.size > 1) partesNombre[1].trim() else ""

                            // Separar nombre y apellido si vienen juntos
                            val partesNombreApellido = nombreCompleto.split(" ", limit = 2)
                            val nombre = if (partesNombreApellido.isNotEmpty()) partesNombreApellido[0] else nombreCompleto
                            val apellido = if (partesNombreApellido.size > 1) partesNombreApellido[1] else ""

                            AcudienteModernoAdapter.AcudienteDisplay(
                                id = id,
                                nombre = nombre,
                                apellido = apellido,
                                correo = correo
                            )
                        }

                        acudienteAdapter.updateAcudientes(acudientesDisplay)
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
        mostrarDialogoAcudiente("Crear Acudiente", null) { nombre, apellido, correo ->
            val acudiente = Acudiente(
                nombre = nombre,
                apellido = apellido,
                correo = correo
            )

            RetrofitInstance.apiAcudientes.crearAcudiente(acudiente).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AcudientesActivity, "Acudiente creado exitosamente", Toast.LENGTH_SHORT).show()
                        mostraracudientes(v)
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
        mostrarDialogoID("Editar Acudiente") { id ->
            mostrarDialogoAcudiente("Editar Acudiente", null) { nombre, apellido, correo ->
                val acudiente = Acudiente(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo
                )

                RetrofitInstance.apiAcudientes.editarAcudiente(id, acudiente).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AcudientesActivity, "Acudiente editado exitosamente", Toast.LENGTH_SHORT).show()
                            mostraracudientes(v)
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
            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este acudiente?")
                .setPositiveButton("Eliminar") { _, _ ->
                    RetrofitInstance.apiAcudientes.eliminarAcudiente(id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AcudientesActivity, "Acudiente eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                mostraracudientes(v)
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

    // MÉTODOS DE DIÁLOGO
    private fun mostrarDialogoAcudiente(
        titulo: String,
        acudiente: Acudiente?,
        callback: (String, String, String) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_acudiente, null)

        val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
        val editApellido = dialogView.findViewById<EditText>(R.id.editApellido)
        val editCorreo = dialogView.findViewById<EditText>(R.id.editCorreo)

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
                    callback(nombre, apellido, correo)
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