package com.tms.financiapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.tms.financiapp.R
import com.tms.financiapp.controllers.UserController
import com.tms.financiapp.helpers.Helper

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        singUp()
        logIn()
        forgotPassword()
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Uy, algo salio mal")
        builder.setMessage("Contrasena o correo incorrecto")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String) {
        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun forgotPassword() {
        val forgotPasswordButton = findViewById<TextView>(R.id.forgotPasswordTV)
        forgotPasswordButton.setOnClickListener{
            val forgotPasswordIntent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(forgotPasswordIntent)
        }
    }

    private fun singUp() {
        title = "Registrarse"
        val registerButton = findViewById<Button>(R.id.registerButton)
        val emailET = findViewById<EditText>(R.id.emailET)
        val passwordET = findViewById<EditText>(R.id.passwordET)
        val helper = Helper()
        registerButton.setOnClickListener {
            if (emailET.text.isNotEmpty() && passwordET.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailET.text.toString(),
                    passwordET.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            it.result.user?.email?.let { it1 ->
                                val newUser = UserController()
                                newUser.createUser(helper.getUserID().toString(), it1)
                                showHome(it1) }
                        }else{
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun logIn(){
        title = "Iniciar Sesion"
        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailET = findViewById<EditText>(R.id.emailET)
        val passwordET = findViewById<EditText>(R.id.passwordET)

        loginButton.setOnClickListener {
            if (emailET.text.isNotEmpty() && passwordET.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailET.text.toString(),
                    passwordET.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        it.result.user?.email?.let { it1 -> showHome(it1) }
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }
}