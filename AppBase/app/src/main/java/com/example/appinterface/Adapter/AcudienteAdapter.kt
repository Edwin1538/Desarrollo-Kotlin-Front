package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Acudiente

class AcudienteAdapter(private val acudientes: List<Acudiente>) : RecyclerView.Adapter<AcudienteAdapter.AcudienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcudienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return AcudienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcudienteViewHolder, position: Int) {
        holder.bind(acudientes[position])
    }

    override fun getItemCount(): Int = acudientes.size

    class AcudienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(acudiente: Acudiente) {
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)
            text1.text = "${acudiente.nombre} ${acudiente.apellido}"
            text2.text = "Tel: ${acudiente.telefono} - Estudiante: ${acudiente.estudiante}"
        }
    }
}