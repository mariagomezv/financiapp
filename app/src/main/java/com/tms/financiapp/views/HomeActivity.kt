package com.tms.financiapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tms.financiapp.R
import com.tms.financiapp.controllers.TransactionAdapter
import com.tms.financiapp.controllers.TransactionController
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.Transaction

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createAccount()
        editProfile()
        addTransaction()
        obtenerTransacciones()
    }

    private fun showEditProfileActivity() {
        val accountActivity: Intent = Intent(this, EditProfileActivity::class.java)
        startActivity(accountActivity)
    }

    private fun showAccountActivity() {
        val accountActivity: Intent = Intent(this, AccountActivity::class.java)
        startActivity(accountActivity)
    }

    private fun createAccount() {
        val createAccountButton = findViewById<Button>(R.id.createAccount)

        createAccountButton.setOnClickListener {
            showAccountActivity()
        }
    }

    private fun editProfile() {
        val createAccountButton = findViewById<Button>(R.id.editProfile)

        createAccountButton.setOnClickListener {
            showEditProfileActivity()
        }
    }

    private fun showAddTransactionActivity() {
        val transactionActivity: Intent = Intent(this, TransactionActivity::class.java)
        startActivity(transactionActivity)
    }

    private fun addTransaction() {
        val addTransactionButton = findViewById<Button>(R.id.addTransaction)

        addTransactionButton.setOnClickListener {
            showAddTransactionActivity()
        }
    }

    private fun obtenerTransacciones() {
        val transactionController = TransactionController()
        val helper = Helper()
        helper.showToast(this, "Hola")
        transactionController.getTransactions(helper.getUserID()) { transactions ->
            // Aquí se llama al callback cuando se obtienen las transacciones
            // Puedes actualizar el RecyclerView con las transacciones obtenidas
            helper.showToast(this, transactions.toString())
            actualizarRecyclerView(transactions)
        }

    }

    fun actualizarRecyclerView(transactions: List<Transaction>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this) // Utiliza el LinearLayoutManager o cualquier otro que desees
        val adapter = TransactionAdapter(transactions)
        recyclerView.adapter = adapter
    }
}