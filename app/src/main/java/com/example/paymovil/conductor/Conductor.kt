package com.example.paymovil.conductor

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.paymovil.MainActivity
import com.example.paymovil.R
import com.example.paymovil.pasajero.AgregarTarjetaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Conductor : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var drawer : DrawerLayout
    private  lateinit var  toogle : ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conductor)
        //Barra de navegacion
        val navigationView : NavigationView = findViewById(R.id.nav_view_conductor)
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
        val actividadFragment = ActividadConductorFragment()
        val inicioFragment = InicioConductorFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.contenedorNavagacionConductor)
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
                else -> false
            }
        }

    }

    fun datosPerfil(){
        val navigationView : NavigationView = findViewById(R.id.nav_view_conductor)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val name = prefs.getString("name", null)
        val email = prefs.getString("email", null)
        val photoUrl = prefs.getString("photoUrl", null)
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl).apply(RequestOptions.circleCropTransform())
                .into(navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_imagen))
        }
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_tipo).text = "Conductor"
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_nombre).text = name
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_correo).text = email
    }
    //Funciones de los fragment, de de escanner, inicio y actividad
    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContenedorConductor, fragment)
            commit()
        }
    }


    //Barra de navegacion Perfil
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_inicio -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContenedorConductor, InicioConductorFragment())
                    commit()
                }
            }
            R.id.nav_item_agregarTarjeta -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContenedorConductor, AgregarTarjetaCondcutorFragment())
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
                        replace(R.id.fragmentContenedorConductor, EditarPerfilConductorFragment())
                        commit()
                    }
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
}