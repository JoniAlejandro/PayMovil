package com.example.paymovil.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymovil.R

class ActividadAdapterConductor  (private var actividad : List<ActividadConductor>): RecyclerView.Adapter<ActividadAdapterConductor.ActividadHolder>(){

    //var messages: List<Message> = messages
    inner class ActividadHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var item_detalle : TextView
        var item_dinero: TextView
        var item_date: TextView
        init {
            item_detalle = itemView.findViewById(R.id.item_detalle)
            item_dinero = itemView.findViewById(R.id.item_dinero)
            item_date = itemView.findViewById(R.id.item_date)
        }
    }
    fun setData(list: List<ActividadConductor>){
        actividad = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad_conductor, parent, false)
        return ActividadHolder(mView)
    }
    override fun onBindViewHolder(holder: ActividadHolder, i: Int) {
        holder.item_detalle.text = "Remitente: " + actividad[i].email
        holder.item_dinero.text = "+ " + actividad[i].precio
        holder.item_date.text = actividad[i].date
    }

    override fun getItemCount(): Int {
        return actividad.size
    }
}