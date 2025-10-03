package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R

class PersonaAdapter(private val personas: List<String>) : RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        // Inflamos TU layout item_docente.xml (o item_acudiente si usas ese)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_docente, parent, false)
        return PersonaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        holder.bind(personas[position])
    }

    override fun getItemCount(): Int = personas.size

    class PersonaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ---- DOCENTE ----
        private val nombreDocente: TextView? = itemView.findViewById(R.id.nombreDocente)
        private val emailDocente: TextView? = itemView.findViewById(R.id.emailDocente)
        private val avatarDocente: ImageView? = itemView.findViewById(R.id.avatarDocente)

        // ---- ACUDIENTE ----
        private val nombreAcudiente: TextView? = itemView.findViewById(R.id.nombreAcudiente)
        private val apellidoAcudiente: TextView? = itemView.findViewById(R.id.apellidoAcudiente)
        private val emailAcudiente: TextView? = itemView.findViewById(R.id.emailAcudiente)
        private val avatarAcudiente: ImageView? = itemView.findViewById(R.id.avatarDocente) // mismo id de imagen

        fun bind(persona: String) {
            // Para DOCENTE
            nombreDocente?.text = persona
            emailDocente?.text = "correo@ejemplo.com"
            avatarDocente?.setImageResource(R.drawable.docente) // imagen de docente

            // Para ACUDIENTE
            nombreAcudiente?.text = persona
            apellidoAcudiente?.text = "Apellido de prueba"
            emailAcudiente?.text = "acudiente@ejemplo.com"
            avatarAcudiente?.setImageResource(R.drawable.acudiente) // imagen de acudiente
        }
    }
}
