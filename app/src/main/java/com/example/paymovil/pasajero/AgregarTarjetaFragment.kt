package com.example.paymovil.pasajero

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paymovil.R
import com.google.firebase.firestore.FirebaseFirestore

class AgregarTarjetaFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val email = prefs.getString("email", null)
        val componentFragment = inflater.inflate(R.layout.fragment_agregar_tarjeta, container, false)
        val tNombre = componentFragment.findViewById<EditText>(R.id.tarjetaNombre).text
        val tNumero = componentFragment.findViewById<EditText>(R.id.tarjetaNumero).text
        val tDate = componentFragment.findViewById<EditText>(R.id.tarjetaDate).text
        val btnAgregar = componentFragment.findViewById<Button>(R.id.btnAgregar)

        btnAgregar.setOnClickListener{

            if (email != null && tNombre.toString().isNotEmpty() && tNumero.toString().isNotEmpty()) {
                db.collection("pasajeros").document(""+ email).collection("tarjetas").document().set(
                    hashMapOf("nombre" to tNombre.toString(), "numeroTarjeta" to tNumero.toString(),
                            "expiracion" to tDate.toString()
                    )
                )

                //Toast.makeText(context,nombre.toString(), Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContenedor, InicioFragment())
                    commit()
                }
            }else{
                Toast.makeText(context,"Campos vacios", Toast.LENGTH_SHORT).show()
            }
        }
        return componentFragment
    }
}
