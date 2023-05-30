package com.example.paymovil

import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.paymovil.conductor.Conductor
import com.example.paymovil.pasajero.Pasajero
import com.example.socialmaps.VerificarInformacion
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso:GoogleSignInOptions
    var googleSignInAccount: GoogleSignInAccount? = null
    companion object{
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sesionActiva()
        setup()
    }
    private fun setup() {
        // Recursos necesarios para logincon Google
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        //Pasajero o conductor
        val bandera = findViewById<Switch>(R.id.switch1)
        // EditText
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val verificar = VerificarInformacion()
        //Botones
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        // Funcion click
        btnLogin.setOnClickListener {
            if (verificar.validarCamposLogin(email, password)) {
                signIn(email.text.toString(), password.text.toString(), bandera)
            }

        }
        btnRegistrarse.setOnClickListener {
            val intentar = Intent(this, Registrarse::class.java)
            startActivity(intentar)
        }

        btnGoogle.setOnClickListener {
            mGoogleSignInClient.signOut()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    private fun sesionActiva(){
        if(FirebaseAuth.getInstance().currentUser != null ){
            showInicio()
        }
    }
    private  fun showInicio(){
        val prefs =
            getSharedPreferences(
                getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            )
        val tipo  = prefs.getString("tipo", null)
        if(tipo == "conductor"){
            intent = Intent(this, Conductor::class.java)
            startActivity(intent)
            finish()
        }
       if(tipo == "pasajero"){
           var intent =  Intent(this, Pasajero::class.java)
           startActivity(intent)
           finish()
       }
    }
    private fun signIn(email: String, password: String, bandera: Switch) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val prefs =
                        getSharedPreferences(
                            getString(R.string.prefs_file),
                            Context.MODE_PRIVATE
                        ).edit()
                    if (bandera.isChecked) {
                        FirebaseFirestore.getInstance().collection("conductor").document(email)
                            .get().addOnSuccessListener {
                            if (it.exists()) {
                                val name = it.get("nombre") as String? + " " + it.get("apellido") as String?
                                prefs.putString("name", name)
                                prefs.putString("email", email)
                                prefs.putString("tipo", "conductor")
                                prefs.apply()
                                showInicio()
                            } else {
                                showAlert("Conductor no existe")
                            }
                        }
                    }else {
                        FirebaseFirestore.getInstance().collection("pasajeros").document(email)
                            .get().addOnSuccessListener {
                            if (it.exists()) {
                                val name = it.get("nombre") as String? + " " + it.get("apellido") as String?
                                prefs.putString("name", name)
                                prefs.putString("email", email)
                                prefs.putString("tipo", "pasajero")
                                prefs.apply()
                                showInicio()
                            } else {
                                showAlert("Pasajero no existe")
                            }
                        }
                    }

                } else {
                    showAlert("No fue posible verificar")
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //val user = Firebase.auth.currentUser
        val bandera = findViewById<Switch>(R.id.switch1)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)
                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val prefs =
                                getSharedPreferences(
                                    getString(R.string.prefs_file),
                                    Context.MODE_PRIVATE
                                ).edit()
                            if(bandera.isChecked){
                                prefs.putString("tipo", "conductor")
                            }else{
                                prefs.putString("tipo", "pasajero")
                            }
                            val user = FirebaseAuth.getInstance().currentUser
                            val name = user?.displayName.toString()
                            val email = user?.email.toString()
                            val photoUrl = user?.photoUrl.toString()
                            prefs.putString("name", name)
                            prefs.putBoolean("google", true)
                            prefs.putString("email", email)
                            prefs.putString("photoUrl", photoUrl)
                            prefs.apply()
                            showInicio()
                        } else {
                            showAlert("Error al acceder a la cuenta")
                        }
                    }
                }
            }catch (e: ApiException){
                //"Error: ${e.message}"
                Toast.makeText(this,"Proceso cancelado", Toast.LENGTH_SHORT).show()
            }
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