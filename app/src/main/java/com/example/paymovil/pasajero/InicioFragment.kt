package com.example.paymovil.pasajero

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymovil.R
import com.example.paymovil.adapters.Tarjeta
import com.example.paymovil.adapters.TarjetaAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InicioFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var lstTarjetas : ArrayList<Tarjeta>
    private lateinit var rvTarjetas : RecyclerView
    private lateinit var mAdapterActividad: TarjetaAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // Inflate the layout for this fragment
        val componentFragment = inflater.inflate(R.layout.fragment_inicio, container, false)
        rvTarjetas = componentFragment.findViewById(R.id.recycleViewInicio)
        lstTarjetas = ArrayList()
        mAdapterActividad = TarjetaAdapter(lstTarjetas)
        rvTarjetas.layoutManager = LinearLayoutManager(context)
        rvTarjetas.adapter = mAdapterActividad
        rvTarjetas.setHasFixedSize(true)
        //
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val correo = prefs.getString("email", null)
        db.collection("pasajeros").document(""+ correo).collection("tarjetas")
            .addSnapshotListener { queryDocumentSnapshots, e ->
                queryDocumentSnapshots?.documentChanges?.forEach { mDocumentChange ->
                    if(mDocumentChange.type == DocumentChange.Type.ADDED){
                        lstTarjetas.add(mDocumentChange.document.toObject(Tarjeta::class.java))
                        mAdapterActividad.notifyDataSetChanged()
                        rvTarjetas.smoothScrollToPosition(lstTarjetas.size)
                    }
                }
            }

        return componentFragment

    }
}