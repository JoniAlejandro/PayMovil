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
import com.example.paymovil.adapters.Actividad
import com.example.paymovil.adapters.ActividadAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ActividadFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var lstActividades : ArrayList<Actividad>
    private lateinit var rvActividades : RecyclerView
    private lateinit var mAdapterActividad: ActividadAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // Inflate the layout for this fragment
        val componentFragment = inflater.inflate(R.layout.fragment_actividad, container, false)
        rvActividades = componentFragment.findViewById(R.id.recycleViewActividad)
        lstActividades = ArrayList()
        mAdapterActividad = ActividadAdapter(lstActividades)
        rvActividades.layoutManager = LinearLayoutManager(context)
        rvActividades.adapter = mAdapterActividad
        rvActividades.setHasFixedSize(true)
        //
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val correo = prefs.getString("email", null)
        db.collection("pasajeros").document(""+ correo).collection("pagos")
            .addSnapshotListener { queryDocumentSnapshots, e ->
                queryDocumentSnapshots?.documentChanges?.forEach { mDocumentChange ->
                    if(mDocumentChange.type == DocumentChange.Type.ADDED){
                        lstActividades.add(mDocumentChange.document.toObject(Actividad::class.java))
                        mAdapterActividad.notifyDataSetChanged()
                        rvActividades.smoothScrollToPosition(lstActividades.size)
                    }
                }
            }
        val bottomNavigationView = LayoutInflater.from(context).inflate(R.layout.activity_pasajero, null)
        val icono = bottomNavigationView.findViewById<BottomNavigationView>(R.id.contenedorNevagacion)
        icono.getOrCreateBadge(R.id.nav_actividad).apply {
            isVisible = false
        }

        return componentFragment

    }
}