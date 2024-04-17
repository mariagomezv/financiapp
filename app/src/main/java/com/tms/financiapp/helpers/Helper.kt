package com.tms.financiapp.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Layout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.tms.financiapp.views.HomeActivity
import java.text.SimpleDateFormat
import java.util.Date

class Helper {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun getUserID(): String {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            return currentUser.uid
        } else {
            throw UserNotFoundException()
        }
    }

    fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

    }

    fun regresarAlMainActivity(activity: Activity) {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Elimina todas las activities del stack excepto la MainActivity
        activity.startActivity(intent)
        activity.finish() // Finaliza la activity actual
    }
}
