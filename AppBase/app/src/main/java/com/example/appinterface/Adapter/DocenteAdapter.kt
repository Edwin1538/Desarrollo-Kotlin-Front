package com.example.appinterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Docente

class DocenteAdapter(private val docentes: List<Docente>) : RecyclerView.Adapter<DocenteAdapter.DocenteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocenteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return DocenteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocenteViewHolder, position: Int) {
        holder.bind(docentes[position])
    }

    override fun getItemCount(): Int = docentes.size

    class DocenteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(docente: Docente) {
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)
            text1.text = "${docente.nombre} ${docente.apellido}"
            text2.text = "Materia: ${docente.materia}"
        }
    }
}