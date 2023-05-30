package com.example.paymovil.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymovil.R

class ActividadAdapter  (private var actividad : List<Actividad>): RecyclerView.Adapter<ActividadAdapter.ActividadHolder>(){

    //var messages: List<Message> = messages
    inner class ActividadHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var item_titulo: TextView
        var item_detalle: TextView
        var item_dinero: TextView
        var item_date: TextView
        init {
            item_titulo = itemView.findViewById(R.id.item_titulo)
            item_detalle = itemView.findViewById(R.id.item_detalle)
            item_dinero = itemView.findViewById(R.id.item_dinero)
            item_date = itemView.findViewById(R.id.item_date)
        }
    }
    fun setData(list: List<Actividad>){
        actividad = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacion, parent, false)
        return ActividadHolder(mView)
    }
    override fun onBindViewHolder(holder: ActividadHolder, i: Int) {
        holder.item_titulo.text = actividad[i].destino
        holder.item_detalle.text = actividad[i].unidad
        holder.item_dinero.text = "- " + actividad[i].precio
        holder.item_date.text = actividad[i].date
    }

    override fun getItemCount(): Int {
        return actividad.size
    }
}