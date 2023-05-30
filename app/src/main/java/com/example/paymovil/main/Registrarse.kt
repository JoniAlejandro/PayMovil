package com.example.paymovil

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.socialmaps.VerificarInformacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registrarse : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)
        //editText
        val nombre = findViewById<EditText>(R.id.reNombre)
        val apellido = findViewById<EditText>(R.id.reApellidos)
        val email = findViewById<EditText>(R.id.reCorreo)
        val password = findViewById<EditText>(R.id.rePassword)
        //Botones
        val verificar = VerificarInformacion()
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        //FirebaseAuth.getInstance().signOut()
        //onBackPressed()
        btnRegistro.setOnClickListener{
            if(verificar.validarCamposRegistro(email, password, nombre, apellido)) {
                createAccount(email.text.toString(), password.text.toString(), nombre.text.toString(), apellido.text.toString())
            }

        }
    }

    fun createAccount(email: String, password: String, nombre: String, apellido: String){

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val prefs =
                    getSharedPreferences(
                        getString(R.string.prefs_file),
                        Context.MODE_PRIVATE
                    ).edit()
                prefs.putString("name", nombre + " " + apellido)
                prefs.putBoolean("google", false)
                prefs.putString("email", email)
                prefs.putString("photoUrl", null)
                prefs.apply()
                db.collection("usuarios").document(email).set(
                    hashMapOf(
                        "nombre" to nombre,
                        "apellido" to apellido
                        //saldo" to "",
                    )
                )
                FirebaseAuth.getInstance().signOut()
            }else{
                showAlert("Error al crear cuenta")
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun showAlert(mensaje : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }
}