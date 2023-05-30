package com.example.paymovil.conductor

import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.paymovil.R
import com.example.paymovil.adapters.Tarjeta
import com.example.paymovil.adapters.TarjetaAdapter
import com.example.socialmaps.VerificarInformacion
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


class InicioConductorFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var lstTarjetas : ArrayList<Tarjeta>
    private lateinit var rvTarjetas : RecyclerView
    private lateinit var mAdapterActividad: TarjetaAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // Inflate the layout for this fragment
        val componentFragment = inflater.inflate(R.layout.fragment_inicio_conductor, container, false)
        var ivCodigoQR  = componentFragment.findViewById<ImageView>(R.id.imagenQr)
        val btnCrearQr = componentFragment.findViewById<Button>(R.id.generarQr)
        val imgGuardar = componentFragment.findViewById<ImageView>(R.id.imgGuardar)
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email", null)
        rvTarjetas = componentFragment.findViewById(R.id.recycleViewInicio)
        lstTarjetas = ArrayList()
        mAdapterActividad = TarjetaAdapter(lstTarjetas)
        rvTarjetas.layoutManager = LinearLayoutManager(context)
        rvTarjetas.adapter = mAdapterActividad
        rvTarjetas.setHasFixedSize(true)
        var mapa : Bitmap? = null
        db.collection("conductor").document(""+ email).collection("tarjetas")
            .addSnapshotListener { queryDocumentSnapshots, e ->
                queryDocumentSnapshots?.documentChanges?.forEach { mDocumentChange ->
                    if(mDocumentChange.type == DocumentChange.Type.ADDED){
                        lstTarjetas.add(mDocumentChange.document.toObject(Tarjeta::class.java))
                        mAdapterActividad.notifyDataSetChanged()
                        rvTarjetas.smoothScrollToPosition(lstTarjetas.size)
                    }
                }
            }

        btnCrearQr.setOnClickListener(){
            db.collection("conductor").document(""+ email).collection("tarjetas")
                .get().addOnSuccessListener {
                    if(it.size()  != 0 ){
                        if (email != null) {
                            showDialog(email)
                        }
                        val tarjetaNumero = prefs.getString("tarjetaNumero", null)
                        val destino = prefs.getString("destino", null)
                        val precio = prefs.getString("precio", null)
                        try {
                            val barcodeEncoder = BarcodeEncoder()
                            val bitmap = barcodeEncoder.encodeBitmap(
                                tarjetaNumero + " " + destino + " " + precio,
                                BarcodeFormat.QR_CODE,
                                170,
                                170
                            )
                            Toast.makeText(context, tarjetaNumero+ " " +precio, Toast.LENGTH_SHORT)
                                .show()
                            mapa = bitmap
                            ivCodigoQR.setImageBitmap(bitmap)
                        } catch (e: Exception) { }
                    }else{
                        alerta()
                    }
                }
        }
        imgGuardar.setOnClickListener{
            if(mapa != null) {
                val savedImageURL = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    mapa,
                    "Movil Pay QR",
                    "Código QR generado"
                )

                if (savedImageURL != null) {
                    Toast.makeText(context, "Código QR guardado en la galería", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Error al guardar el código QR", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                Toast.makeText(context, "Genera el QR", Toast.LENGTH_SHORT).show()
            }
        }

        return componentFragment
    }

    fun showDialog(email :  String) {
        val verificar = VerificarInformacion()
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_conductor, null)
        val tarjetaNumero  = dialogView.findViewById<EditText>(R.id.tarjetaNumero)
        val destino = dialogView.findViewById<EditText>(R.id.destino)
        val precio = dialogView.findViewById<EditText>(R.id.precio)
        val btnSalir = dialogView.findViewById<Button>(R.id.btnSalir)
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        btnSalir.setOnClickListener {
            if(verificar.validarCamposQR(tarjetaNumero, destino, precio)){
                prefs.putString("destino", destino.text.toString())
                prefs.putString("precio", precio.text.toString())
                prefs.putString("tarjetaNumero", tarjetaNumero.text.toString())
                prefs.apply()
                db.collection("conductor").document(""+ email).set(
                    hashMapOf("destino" to destino.text.toString(), "tarjetaNumero" to tarjetaNumero.text.toString(),
                        "precio" to precio.text.toString()
                    )
                )
                dialog.dismiss()
            }

        }
        dialog.show()
    }

    fun alerta() {
        val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_tarjeta, null)
        val btnSalir = dialogView.findViewById<Button>(R.id.btnSalir)
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        btnSalir.setOnClickListener {
                dialog.dismiss()
        }
        dialog.show()
    }
}