package com.tms.financiapp.views

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tms.financiapp.R
import com.tms.financiapp.controllers.UserController
import com.tms.financiapp.helpers.EnumAdapter
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.User
import com.tms.financiapp.models.enums.IdType

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editProfile()
    }

    private fun editProfile() {
        val nameEditText = findViewById<EditText>(R.id.nameET)
        val spinnerIdType = findViewById<Spinner>(R.id.spinnerIdType)
        val idNumber = findViewById<EditText>(R.id.identificationNumber)
        val updateButton = findViewById<Button>(R.id.updateBT)

        val helper = Helper()

        val idTypeAdapter = EnumAdapter<IdType>(this, IdType::class.java)
        spinnerIdType.adapter = idTypeAdapter

        val userId = helper.getUserID()

        val userController = UserController()

        userId.let {
            userController.getUser(it) { user ->
                // Set the user data in the UI elements
                nameEditText.setText(user.name)
                idNumber.setText(user.idNumber)

                updateButton.setOnClickListener {
                    val name = nameEditText.text.toString()
                    val documentType = (spinnerIdType.selectedItem as IdType).code
                    val documentNumber = idNumber.text.toString()

                    // Update the user data
                    userController.updateUser(
                        user.id,
                        User(
                            id = user.id,
                            name = name,
                            idType = documentType,
                            idNumber = documentNumber
                        )
                    )

                    helper.regresarAlMainActivity(this)
                }
            }
        }
    }
}