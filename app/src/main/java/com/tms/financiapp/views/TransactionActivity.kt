package com.tms.financiapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tms.financiapp.R
import com.tms.financiapp.controllers.BankAccountController
import com.tms.financiapp.controllers.TransactionController
import com.tms.financiapp.helpers.EnumAdapter
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.Transaction
import com.tms.financiapp.models.enums.PurchaseCategory
import com.tms.financiapp.models.enums.TransactionType

class TransactionActivity : AppCompatActivity() {

    private lateinit var contentContainer: FrameLayout
    private val bankAccountController = BankAccountController()
    private val transactionController = TransactionController()
    private lateinit var currentTransactionType: TransactionType
    private val helper = Helper()
    private var accountNumbers = emptyList<String>()
    private var allAccountNumbers = emptyList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        val progressBarTransaction = findViewById<ProgressBar>(R.id.transactioprogressBar)
        enableEdgeToEdge()

        val buttons = listOf<Button>(
            findViewById(R.id.button_one),
            findViewById(R.id.button_two),
            findViewById(R.id.button_three)
        )


        contentContainer = findViewById(R.id.content_container)
        getAllBankAccounts()
        getBankAccountsPerUser()

        buttons.forEachIndexed { index, button ->
            button.isEnabled = false
            button.postDelayed({
                button.isEnabled = true
                progressBarTransaction.visibility = View.INVISIBLE
            }, 7000)
            button.setOnClickListener {
                val layoutId = when (index) {
                    0 -> R.layout.activity_deposit
                    1 -> R.layout.activity_withdrawal
                    2 -> R.layout.activity_transfer
                    else -> R.layout.activity_deposit
                }
                inflateLayout(layoutId, true)
                if (index == 0) {
                    updateSpinnersDeposit(accountNumbers)
                    currentTransactionType = TransactionType.DEPOSIT
                    val depositButtonSend = findViewById<Button>(R.id.sendDepositBT)
                    depositButtonSend.setOnClickListener {
                        val userId = helper.getUserID()
                        val spinnerAccountDeposit = findViewById<Spinner>(R.id.cuentaDSP)
                        val spinnerCategoryDeposit = findViewById<Spinner>(R.id.categoryDSP)
                        val amount = findViewById<EditText>(R.id.amountDET)
                        val date = helper.getCurrentDateString()
                        val category = spinnerCategoryDeposit.selectedItem.toString()
                        val description = " Deposito"

                        val transaction = Transaction(
                            id = 1,
                            userId = userId,
                            transactionType = currentTransactionType.code,
                            amount = amount.text.toString().toDouble(),
                            date = date,
                            category = category,
                            description = "$category $description",
                            account = spinnerAccountDeposit.selectedItem.toString(),
                            toAccount = ""
                        )
                        transactionController.addTransaction(transaction)
                        helper.regresarAlMainActivity(this)
                    }
                }
                if (index == 1) {
                    updateSpinnersWD(accountNumbers)
                    currentTransactionType = TransactionType.WITHDRAWAL
                    val withDrawalButtonSend = findViewById<Button>(R.id.withDrawalBT)
                    withDrawalButtonSend.setOnClickListener{
                        val userId = helper.getUserID()
                        val spinnerAccountWithdrawal = findViewById<Spinner>(R.id.cuentaWSP)
                        val spinnerCategoryWithdrawal = findViewById<Spinner>(R.id.categoryWSP)
                        val amount =  findViewById<EditText>(R.id.amountWET)
                        val date = helper.getCurrentDateString()
                        val category = spinnerCategoryWithdrawal.selectedItem.toString()
                        val description = "Retiro"

                        val transaction = Transaction(
                            id = 1,
                            userId = userId,
                            transactionType = currentTransactionType.code,
                            amount = amount.text.toString().toDouble(),
                            date = date,
                            category = category,
                            description = "$category $description",
                            account = spinnerAccountWithdrawal.selectedItem.toString(),
                            toAccount = ""
                        )
                        transactionController.addTransaction(transaction)
                        helper.regresarAlMainActivity(this)
                    }
                }
                if (index == 2) {
                    updateSpinnersTransfer(accountNumbers, allAccountNumbers)
                    currentTransactionType = TransactionType.TRANSFER
                    val transactionSendButton = findViewById<Button>(R.id.sendTransferBT)
                    transactionSendButton.setOnClickListener {
                        val userId = helper.getUserID()
                        val spinnerAccountFrom = findViewById<Spinner>(R.id.cuentaTSP)
                        val spinnerCategory = findViewById<Spinner>(R.id.categoryTSP)
                        val editTextToAccount = findViewById<EditText>(R.id.reciverCuentaT)
                        val amount = findViewById<EditText>(R.id.amountDET)
                        val date = helper.getCurrentDateString()
                        val category = spinnerCategory.selectedItem.toString()
                        val description = "Transferencia"

                        val transaction = Transaction(
                            id = 1,
                            userId = userId,
                            transactionType = currentTransactionType.code,
                            amount = amount.text.toString().toDouble(),
                            date = date,
                            category = category,
                            description = "$category $description",
                            account = spinnerAccountFrom.selectedItem.toString(),
                            toAccount = editTextToAccount.text.toString()
                        )

                        if (transaction.toAccount in allAccountNumbers){
                            transactionController.addTransaction(transaction)
                            helper.regresarAlMainActivity(this)
                        } else {
                            helper.showAlert(this, "Error", "Cuenta no existe")
                        }
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inflateLayout(layoutId: Int, visible: Boolean) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(layoutId, null)
        contentContainer.removeAllViews()
        contentContainer.addView(layout)
        contentContainer.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun updateSpinnersDeposit(bankAccounts: List<String>) {
        val spinnerAccountDeposit = findViewById<Spinner>(R.id.cuentaDSP)
        val spinnerCategoryDeposit = findViewById<Spinner>(R.id.categoryDSP)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankAccounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountDeposit.adapter = adapter


        val purchaseCategory = EnumAdapter(this, PurchaseCategory::class.java)
        spinnerCategoryDeposit.adapter = purchaseCategory
    }

    private fun getBankAccountsPerUser() {
        bankAccountController.getBankAccounts(helper.getUserID()) { accounts ->
            val accountNumbersList = accounts.map { it.accountNumber }
            accountNumbers = accountNumbersList
        }
    }

    private fun getAllBankAccounts() {
        bankAccountController.fetchAccountsFromFirestore{ accounts ->
            val accountNumbersList = accounts.map { it.accountNumber }
            allAccountNumbers = accountNumbersList
        }
    }


    private fun updateSpinnersWD(bankAccounts: List<String>) {
        val spinnerAccountWD = findViewById<Spinner>(R.id.cuentaWSP)
        val spinnerCategoryWD = findViewById<Spinner>(R.id.categoryWSP)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankAccounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountWD.adapter = adapter

        val purchaseCategory = EnumAdapter(this, PurchaseCategory::class.java)
        spinnerCategoryWD.adapter = purchaseCategory
    }

    private fun updateSpinnersTransfer(bankAccounts: List<String>, allBankAccount: List<String>) {
        val spinnerAccountWD = findViewById<Spinner>(R.id.cuentaTSP)
        val spinnerCategoryWD = findViewById<Spinner>(R.id.categoryTSP)

       // helper.showToast(this, bankAccounts.toString())
       // helper.showToast(this, allBankAccount.toString())

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankAccounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountWD.adapter = adapter

        val purchaseCategory = EnumAdapter(this, PurchaseCategory::class.java)
        spinnerCategoryWD.adapter = purchaseCategory
    }

}
