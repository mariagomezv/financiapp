package com.tms.financiapp.controllers

import com.google.firebase.firestore.FirebaseFirestore
import com.tms.financiapp.models.Transaction
import com.tms.financiapp.models.User
import com.google.firebase.firestore.FieldValue
import com.tms.financiapp.models.enums.TransactionType
import java.text.SimpleDateFormat

class TransactionController {

    private val db = FirebaseFirestore.getInstance()
    val bankAccountController = BankAccountController()

    // Add a new transaction to a user's account
    fun addTransaction(transaction: Transaction) {
        val transactionRef = db.collection("transactions").document()
        transactionRef.set(transaction)

        // Update the user's account balance
        val userRef = db.collection("users").document(transaction.userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                val updatedBankAccounts = user?.bankAccounts?.map { account ->
                    if (account.accountNumber == transaction.account) {
                        when (transaction.transactionType) {
                            TransactionType.DEPOSIT.code -> {
                                val newBalance = account.balance + transaction.amount
                                bankAccountController.updateBalanceInAccounts(account.accountNumber, newBalance)
                                account.copy(balance = newBalance)
                            }
                            TransactionType.WITHDRAWAL.code -> {
                                if (account.balance >= transaction.amount) {
                                    val newBalance = account.balance - transaction.amount
                                    bankAccountController.updateBalanceInAccounts(account.accountNumber, newBalance)
                                    account.copy(balance = newBalance)
                                } else {
                                    // Insufficient balance, return the account unchanged
                                    account
                                }
                            }
                            TransactionType.TRANSFER.code -> {
                                if (account.balance >= transaction.amount) {
                                    val newBalance = account.balance - transaction.amount
                                    bankAccountController.getBalanceByAccountNumber(transaction.toAccount){ balance ->
                                        if (balance != null) {
                                            // Manejar el balance de la cuenta
                                            val newBalanceReciver = balance + transaction.amount
                                            bankAccountController.updateBalanceInAccounts(transaction.toAccount, newBalanceReciver)
                                        } else {
                                            // La cuenta no fue encontrada o hubo un error
                                            println("No se pudo obtener el balance de la cuenta 123456")
                                        }
                                    }
                                    bankAccountController.updateBalanceInAccounts(account.accountNumber, newBalance)
                                } else {
                                    // Insufficient balance, return the account unchanged
                                    account
                                }
                            }
                            else -> {
                                // Invalid transaction type, return the account unchanged
                                account
                            }
                        }
                    } else {
                        // Account not affected by the transaction, return it unchanged
                        account
                    }
                }
                userRef.update("bankAccounts", updatedBankAccounts)
                userRef.update("transactions", FieldValue.arrayUnion(transaction))
            }
        }
    }

    // Get the transactions for a user
    fun getTransactions(userId: String, callback: (List<Transaction>) -> Unit) {
        val transactionRef = db.collection("transactions")
        transactionRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val transactions = mutableListOf<Transaction>()
                for (document in result) {
                    transactions.add(document.toObject(Transaction::class.java))
                }
                transactions.sortByDescending { (SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(it.date)).toString()}
                callback(transactions)
            }
    }
}