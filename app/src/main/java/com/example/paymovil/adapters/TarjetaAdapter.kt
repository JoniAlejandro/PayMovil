package com.example.paymovil.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymovil.R

class TarjetaAdapter   (private var tarjeta : List<Tarjeta>): RecyclerView.Adapter<TarjetaAdapter.TarjetaHolder>(){

    //var messages: List<Message> = messages
    inner class TarjetaHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var numero: TextView
        var nombre: TextView
        var date: TextView
        init {
            numero = itemView.findViewById(R.id.card_number)
            nombre = itemView.findViewById(R.id.cardholder_name)
            date = itemView.findViewById(R.id.date)
        }
    }
    fun setData(list: List<Tarjeta>){
        tarjeta = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarjetaHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_tarjeta, parent, false)
        return TarjetaHolder(mView)
    }
    override fun onBindViewHolder(holder: TarjetaHolder, i: Int) {
        holder.numero.text = tarjeta[i].numeroTarjeta
        holder.nombre.text = tarjeta[i].nombre
        holder.date.text = tarjeta[i].expiracion
    }

    override fun getItemCount(): Int {
        return tarjeta.size
    }
}