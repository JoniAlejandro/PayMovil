package com.example.socialmaps

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

class VerificarInformacion {

    private fun validarApellido(apellido : EditText): Boolean {
        return if(apellido.text.toString().isEmpty()){
            apellido.error = "Apellido vacio"
            false
        }else{
            true
        }
    }
    private fun validarNombre(nombre : EditText): Boolean {
        return if(nombre.text.toString().isEmpty()){
            nombre.error = "Nombre vacio"
            false
        }else{
            true
        }
    }
    private fun validarTarjeta(tarjeta : EditText): Boolean {
        return if(tarjeta.text.toString().isEmpty()){
            tarjeta.error = "Campo vacio"
            false
        }else{
            true
        }
    }

    private fun validarDestino(destino : EditText): Boolean {
        return if(destino.text.toString().isEmpty()){
            destino.error = "Campo vacio"
            false
        }else{
            true
        }
    }
    private fun validarPrecio(precio : EditText): Boolean {
        return if(precio.text.toString().isEmpty()){
            precio.error = "Campo vacio"
            false
        }else{
            true
        }
    }
    private fun validarEmail(email : EditText) : Boolean{
        return if(email.text.toString().isEmpty()){
            email.error = "Correo no debe estar vacio"
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = "Genere un correo valido"
            false
        }else{
            email.error = null
            true
        }

    }
    private fun validarPassword(password : EditText) : Boolean{
        val passwordRegex = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$"
        )
        return if(password.text.toString().isEmpty()){
            password.error = "Contaseña no debe estar vacio"
            false
        }else if(!passwordRegex.matcher(password.text.toString()).matches()){
            password.error = "Contraseña invalida"
            false
        }else{
            password.error = null
            true
        }

    }

    fun validarCamposRegistro(email : EditText, password : EditText, nombre : EditText, apellido: EditText) : Boolean{
        val result = arrayOf(validarEmail(email), validarPassword(password), validarNombre(nombre), validarApellido(apellido))
        return false !in result
    }
    fun validarCamposLogin(email:EditText, password: EditText) : Boolean{
        val result = arrayOf(validarEmail(email), validarPassword(password))
        return false !in result
    }

    fun validarCamposQR(tarjetaNumero : EditText, destino : EditText, precio : EditText) : Boolean{
        val result = arrayOf(validarTarjeta(tarjetaNumero), validarDestino(destino), validarPrecio(precio))
        return false !in result
    }

}