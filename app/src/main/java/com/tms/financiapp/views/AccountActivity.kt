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
import com.tms.financiapp.controllers.BankAccountController
import com.tms.financiapp.controllers.UserController
import com.tms.financiapp.helpers.EnumAdapter
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.BankAccount
import com.tms.financiapp.models.enums.AccountType
import com.tms.financiapp.models.enums.IdType
import com.tms.financiapp.models.User

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createAccount()
    }

    private fun createAccount(){
        val createAccountButton = findViewById<Button>(R.id.createAccount)
        val spinnerAccountType = findViewById<Spinner>(R.id.spinnerAccountType)
        val initialBalance = findViewById<EditText>(R.id.balanceValue)
        val accountTypeAdapter = EnumAdapter<AccountType>(this, AccountType::class.java)
        spinnerAccountType.adapter = accountTypeAdapter


        val helper = Helper()
        val accountController = BankAccountController()
        val userId =helper.getUserID()

        createAccountButton.setOnClickListener{
            val selectedAccountType = spinnerAccountType.selectedItem as AccountType
            val bankAccount = BankAccount(
                accountType = selectedAccountType.code,
                userId = userId,
                accountNumber = (0..9999).random().toString(),
                balance = initialBalance.text.toString().toDouble(),
                isActive = true,
                openDateAccount = helper.getCurrentDateString()
            )
            accountController.addBankAccount(userId, bankAccount)
            helper.regresarAlMainActivity(this)
        }
    }
}