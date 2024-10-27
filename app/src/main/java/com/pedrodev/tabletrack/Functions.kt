package com.pedrodev.tabletrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import java.net.InetAddress

object Functions {

    // NAVEGACIÓN ****************************************************************************

    fun Context.moveTo(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
    // ejemplo:     moveTo(LoginActivity::class.java)

    // CERRAR TECLADO EN PANTALLA ************************************************************
    fun Activity.closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    // MOSTRAR MENSAJES AL USUARIO ***********************************************************
    // ej:    alert("Ingreso fallido.")
    fun View.alert(message: String) {
        Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
    }
    /*
    fun Context.alert(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
     */


    // VALIDACIONES **************************************************************************

    // Formato de correo electrónico
    fun checkValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Dominio de correo electrónico
    fun checkValidDomain(email: String): Boolean {
        val domain = email.substringAfter("@")
        return try {
            val inetAddress = InetAddress.getByName(domain)
            val isReachable = inetAddress.isReachable(5000)
            isReachable
        } catch (e: Exception) {
            false
        }
    }


    // MANEJO DE DATOS **************************************************************************

    fun saveString(context: Context, fileName: String, dataName: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(dataName, value)
        editor.apply()
    }

    fun getString(context: Context, fileName: String, dataName: String): String? {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(dataName, null)
    }

    fun saveBoolean(context: Context, fileName: String, dataName: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(dataName, value)
        editor.apply()
    }

    fun getBoolean(context: Context, fileName: String, dataName: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(dataName, false)
    }

    fun deleteData(context: Context, fileName: String, dataName: String) {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(dataName)
        editor.apply()
    }

    fun clearData(context: Context, fileName: String) {
        val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}