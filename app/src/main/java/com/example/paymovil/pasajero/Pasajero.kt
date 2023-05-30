package com.example.paymovil.pasajero

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.paymovil.MainActivity
import com.example.paymovil.R
import com.example.paymovil.adapters.Actividad
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import java.util.Calendar
import java.text.SimpleDateFormat

class Pasajero : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var drawer : DrawerLayout
    private  lateinit var  toogle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasajero)
        //Barra de navegacion
        val navigationView : NavigationView = findViewById(R.id.nav_view)
        val toolbar :  androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toogle = ActionBarDrawerToggle(this,drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navigationView.setNavigationItemSelectedListener (this)
        datosPerfil()
        setup()
    }
    fun setup(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val actividadFragment = ActividadFragment()
        val inicioFragment = InicioFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.contenedorNevagacion)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_inicio -> {
                    setCurrentFragment(inicioFragment)
                    true
                }
                R.id.nav_actividad -> {
                    setCurrentFragment(actividadFragment)
                    true
                }
                R.id.nav_qr -> {
                    db.collection("pasajeros").document(""+ email).collection("tarjetas")
                        .get().addOnSuccessListener {
                            if(it.size()  != 0 ) {
                                initScanner()
                            }else{
                                alerta()
                            }
                        }
                    true
                }
                else -> false
            }
        }

    }
    fun datosPerfil(){
        val navigationView : NavigationView = findViewById(R.id.nav_view)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val name = prefs.getString("name", null)
        val email = prefs.getString("email", null)
        val photoUrl = prefs.getString("photoUrl", null)
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl).apply(RequestOptions.circleCropTransform())
                .into(navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_imagen))
        }
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_tipo).text = "Pasajero"
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_nombre).text = name
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_correo).text = email
    }
    //Funciones de los fragment, de de escanner, inicio y actividad
    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContenedor, fragment)
            commit()
        }
    }
    private fun initScanner(){
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea tu codigo")
        integrator.initiateScan()
        //flash in integrator.setTorchEnabled(true)
        //sonidito integrator.setBeepEnable(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val calendario = Calendar.getInstance()
        val fechaActual = SimpleDateFormat("dd/MM/yyyy").format(calendario.time)
        val horaActual = SimpleDateFormat("HH:mm:ss").format(calendario.time)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val correo = prefs.getString("email", null)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado ", Toast.LENGTH_LONG).show()
            } else {
                var string = result.contents.split(" ")
                db.collection("pasajeros").document(""+ correo).collection("pagos").document().set(
                    hashMapOf("destino" to string[0], "unidad" to string[1],
                        "precio" to string[2],
                        "date" to horaActual  + " " + fechaActual
                    )
                )
                db.collection("conductor").document(""+ correo).collection("pagos").document().set(
                    hashMapOf( "email" to correo,
                        "precio" to string[2],
                        "date" to horaActual  + " " + fechaActual
                    )
                )
                showDialog(string[0], string[1], string[2])
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //Barra de navegacion Perfil
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_inicio -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContenedor, InicioFragment())
                    commit()
                }
            }
            R.id.nav_item_cerrarSesion -> {
                val intentar = Intent(this, MainActivity::class.java)
                startActivity(intentar)
                FirebaseAuth.getInstance().signOut()
                val prefs = this.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()
                finish()
            }
            R.id.nav_item_editarPerfil -> {
                supportFragmentManager.beginTransaction().apply {
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
                    val google = prefs.getBoolean("google", false)
                    if(google){
                        Toast.makeText(applicationContext,"Modifica tu cuenta de google", Toast.LENGTH_SHORT).show()
                    }else {
                        replace(R.id.fragmentContenedor, EditarPerfil())
                        commit()
                    }
                }
            }
            R.id.nav_item_agregarTarjeta -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContenedor, AgregarTarjetaFragment())
                    commit()
                }
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toogle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toogle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        if(toogle.onOptionsItemSelected(item)){
            return true
        }
    }

    //Alerta de transferenia
    fun showDialog(unidad : String, destino : String, precio :String ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_pago, null)
        val monto = dialogView.findViewById<TextView>(R.id.monto)
        val tipo = dialogView.findViewById<TextView>(R.id.tvTipo)
        val ruta = dialogView.findViewById<TextView>(R.id.tvdestino)
        val btnSalir = dialogView.findViewById<Button>(R.id.btnSalir)
        monto.text = precio
        ruta.text = destino
        tipo.text = unidad
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        btnSalir.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun alerta() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_tarjeta, null)
        val btnSalir = dialogView.findViewById<Button>(R.id.btnSalir)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        btnSalir.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}