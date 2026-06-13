package com.prestutti.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager@Inject constructor(@ApplicationContext context: Context) {
    //Creamos o abrimos el archivo de preferencias
    private val prefs: SharedPreferences = context.getSharedPreferences("prestutti_prefs", Context.MODE_PRIVATE)

    //Función que guarda cuando el usuario se logueó
    fun guardarLoginState(isLoggedIn: Boolean){
        prefs.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    //Función que pregunta si el usuario ya está logueado, por defecto es False
    fun estaLogueado(): Boolean{
        return prefs.getBoolean("isLoggedIn", false)
    }

    //Guardar datos del usuario cuando se registra
    fun registrarUsuario(usuario: String, password: String){
        prefs.edit()
            .putString("saved_username", usuario)
            .putString("saved_password", password)
            .apply()
    }

    //Recupera el usuario guardado (devuelve null si nadie se ha registrado)
    fun obtenerUsuarioRegistrado(): String? {
        return prefs.getString("saved_username", null)
    }

    //Recupera la password guardada
    fun obtenerPasswordRegistrada(): String? {
        return prefs.getString("saved_password", null)
    }

}