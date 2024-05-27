package com.tms.financiapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tms.financiapp.R
import com.tms.financiapp.controllers.BankAccountController
import com.tms.financiapp.controllers.TransactionAdapter
import com.tms.financiapp.controllers.TransactionController
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.Transaction

class HomeActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
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
        viewCharts()
        editProfile()
        addTransaction()
        obtenerTransacciones()
        setupAccountBalanceSection()
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            actualizarVista()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /*private fun setupPieChartButton() {
        val pieChartButton = findViewById<Button>(R.id.viewPieChart)
        pieChartButton.setOnClickListener {
            obtenerTransacciones { transactions ->
                showPieChartActivity(transactions)
            }
        }
    }*/

    /*private fun showPieChartActivity(transactions: List<Transaction>) {
        val intent = Intent(this, ChartActivity::class.java)
        intent.putParcelableArrayListExtra("transactions", ArrayList(transactions))
        startActivity(intent)
    }*/

    private fun actualizarVista() {
        obtenerTransacciones()
        setupAccountBalanceSection()
    }
    private fun showEditProfileActivity() {
        val accountActivity: Intent = Intent(this, EditProfileActivity::class.java)
        startActivity(accountActivity)
    }

    private fun showAccountActivity() {
        val accountActivity: Intent = Intent(this, AccountActivity::class.java)
        startActivity(accountActivity)
    }

    private fun showChartActivity() {
        val chartActivity: Intent = Intent(this, ChartActivity::class.java)
        startActivity(chartActivity)
    }

    private fun createAccount() {
        val createAccountButton = findViewById<Button>(R.id.createAccount)

        createAccountButton.setOnClickListener {
            showAccountActivity()
        }
    }

    private fun viewCharts() {
        val chartActivityButton = findViewById<Button>(R.id.viewChart)

        chartActivityButton.setOnClickListener {
            showChartActivity()
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
        transactionController.getTransactions(helper.getUserID()) { transactions ->
            // Aquí se llama al callback cuando se obtienen las transacciones
            // Puedes actualizar el RecyclerView con las transacciones obtenidas
            actualizarRecyclerView(transactions)
        }
    }

    private fun setupAccountBalanceSection() {
        val accountBalanceContainer = findViewById<LinearLayout>(R.id.account_balance_container)

        // Obtener las cuentas del usuario
        val userId = Helper().getUserID() // Reemplaza esto con la función que obtiene el ID del usuario autenticado
        BankAccountController().getBankAccounts(userId) { accounts ->
            val accountNumbers = accounts.map { it.accountNumber }
            val accountBalances = accounts.map { it.balance }
            val totalBalance = accountBalances.sum()

            // Configurar el Spinner
            val spinnerAccounts = accountBalanceContainer.findViewById<Spinner>(R.id.spinner_accounts)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountNumbers)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAccounts.adapter = adapter

            // Configurar el listener del Spinner
            spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedBalance = accountBalances[position]
                    val tvAccountBalance = accountBalanceContainer.findViewById<TextView>(R.id.tv_account_balance)
                    tvAccountBalance.text = resources.getString(R.string.account_balance, selectedBalance)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Mostrar el saldo total
            val tvTotalBalance = accountBalanceContainer.findViewById<TextView>(R.id.tv_total_balance)
            tvTotalBalance.text = resources.getString(R.string.total_balance_amount, totalBalance)
        }
    }

    fun actualizarRecyclerView(transactions: List<Transaction>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this) // Utiliza el LinearLayoutManager o cualquier otro que desees
        val adapter = TransactionAdapter(transactions)
        recyclerView.adapter = adapter
    }
}