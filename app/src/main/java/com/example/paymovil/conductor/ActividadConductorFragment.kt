package com.example.paymovil.conductor

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
import com.example.paymovil.adapters.ActividadAdapterConductor
import com.example.paymovil.adapters.ActividadConductor
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ActividadConductorFragment : Fragment() {
    private var db = Firebase.firestore
    private lateinit var lstActividadesConductor: ArrayList<ActividadConductor>
    private lateinit var rvActividadesConductor: RecyclerView
    private lateinit var mAdapterActividadConductor: ActividadAdapterConductor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val componentFragment =
            inflater.inflate(R.layout.fragment_actividad_conductor, container, false)
        rvActividadesConductor = componentFragment.findViewById(R.id.recycleViewActividadConductor)
        lstActividadesConductor = ArrayList()
        mAdapterActividadConductor = ActividadAdapterConductor(lstActividadesConductor)
        rvActividadesConductor.layoutManager = LinearLayoutManager(context)
        rvActividadesConductor.adapter = mAdapterActividadConductor
        rvActividadesConductor.setHasFixedSize(true)
        //
        val prefs = this.requireActivity().getSharedPreferences(
            getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val correo = prefs.getString("email", null)
        db.collection("conductor").document("" + correo).collection("pagos")
            .addSnapshotListener { queryDocumentSnapshots, e ->
                queryDocumentSnapshots?.documentChanges?.forEach { mDocumentChange ->
                    if (mDocumentChange.type == DocumentChange.Type.ADDED) {
                        lstActividadesConductor.add(mDocumentChange.document.toObject(ActividadConductor::class.java))
                        mAdapterActividadConductor.notifyDataSetChanged()
                        rvActividadesConductor.smoothScrollToPosition(lstActividadesConductor.size)
                    }
                }
            }

        return componentFragment

    }
}