package com.tms.financiapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.tms.financiapp.R
import com.tms.financiapp.helpers.Helper

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        forgotPassword()
    }

    private fun showAuth() {
        val authIntent: Intent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }
    private fun forgotPassword() {
        val emailResetET = findViewById<EditText>(R.id.emailResetET)
        val sendEmailButton = findViewById<Button>(R.id.sendButton)
        val helper = Helper()

        sendEmailButton.setOnClickListener{
            if (emailResetET.text.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailResetET.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            helper.showAlert(this, "Enviado", "Email de restablecimiento enviado con exito")
                            showAuth()
                        } else {
                            helper.showAlert(this, "Error", "Error de restablecimiento")
                            showAuth()
                        }
                    }
            } else {
                helper.showAlert(this, "Error", "Error de restablecimiento")
            }
        }

    }
}