package com.tms.financiapp.controllers

import com.google.firebase.firestore.FirebaseFirestore
import com.tms.financiapp.models.Transaction
import com.tms.financiapp.models.User
import com.tms.financiapp.models.BankAccount
import com.google.firebase.firestore.FieldValue
import com.tms.financiapp.models.enums.TransactionType
import java.text.SimpleDateFormat
import android.util.Log

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
                if (user != null) {
                    val updatedBankAccounts = user.bankAccounts.map { account ->
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
                                        account // Insufficient balance, return the account unchanged
                                    }
                                }
                                TransactionType.TRANSFER.code -> {
                                    if (account.balance >= transaction.amount) {
                                        val newBalance = account.balance - transaction.amount
                                        bankAccountController.updateBalanceInAccounts(account.accountNumber, newBalance)
                                        account.copy(balance = newBalance)
                                    } else {
                                        account // Insufficient balance, return the account unchanged
                                    }
                                }
                                else -> {
                                    account // Invalid transaction type, return the account unchanged
                                }
                            }
                        } else {
                            account // Account not affected by the transaction, return it unchanged
                        }
                    }

                    // Verifica si updatedBankAccounts no contiene valores nulos
                    if (updatedBankAccounts.isNotEmpty()) {
                        userRef.update("bankAccounts", updatedBankAccounts)
                    } else {
                        Log.e("TransactionController", "Error: updatedBankAccounts contiene valores no serializables.")
                    }

                    // Verificar que el objeto transaction sea serializable
                    val transactionMap = mapOf(
                        "userId" to transaction.userId,
                        "account" to transaction.account,
                        "amount" to transaction.amount,
                        "date" to transaction.date,
                        "transactionType" to transaction.transactionType,
                        "toAccount" to transaction.toAccount
                    )

                    userRef.update("transactions", FieldValue.arrayUnion(transactionMap))

                    // Si es una transferencia, actualiza también la cuenta de destino
                    if (transaction.transactionType == TransactionType.TRANSFER.code) {
                        updateDestinationAccount(transaction.toAccount, transaction.amount)
                    }
                } else {
                    Log.e("TransactionController", "Error: el usuario es nulo.")
                }
            } else {
                Log.e("TransactionController", "Error: el documento del usuario no existe.")
            }
        }.addOnFailureListener { exception ->
            Log.e("TransactionController", "Error al obtener el documento del usuario: ${exception.message}")
        }
    }

    private fun updateDestinationAccount(toAccount: String, amount: Double) {
        // Encuentra la cuenta de destino por su número de cuenta
        val accountsRef = db.collection("accounts")
        accountsRef.whereEqualTo("accountNumber", toAccount).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("TransactionController", "Error: no se encontró la cuenta de destino.")
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val account = document.toObject(BankAccount::class.java)
                    val newBalance = account.balance + amount

                    // Actualiza el balance en la colección de cuentas
                    accountsRef.document(document.id).update("balance", newBalance)

                    // Usa el userId para actualizar la cuenta bancaria en el documento del usuario
                    val userRef = db.collection("users").document(account.userId)
                    userRef.get().addOnSuccessListener { userDoc ->
                        if (userDoc.exists()) {
                            val user = userDoc.toObject(User::class.java)
                            if (user != null) {
                                val updatedBankAccounts = user.bankAccounts.map { userAccount ->
                                    if (userAccount.accountNumber == toAccount) {
                                        userAccount.copy(balance = newBalance)
                                    } else {
                                        userAccount
                                    }
                                }

                                if (updatedBankAccounts.isNotEmpty()) {
                                    userRef.update("bankAccounts", updatedBankAccounts)
                                } else {
                                    Log.e("TransactionController", "Error: updatedBankAccounts contiene valores no serializables.")
                                }
                            } else {
                                Log.e("TransactionController", "Error: el usuario es nulo.")
                            }
                        } else {
                            Log.e("TransactionController", "Error: el documento del usuario no existe.")
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("TransactionController", "Error al obtener el documento del usuario: ${exception.message}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TransactionController", "Error al obtener el documento de la cuenta de destino: ${exception.message}")
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
                transactions.sortByDescending { SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(it.date) }
                callback(transactions)
            }
            .addOnFailureListener { exception ->
                Log.e("TransactionController", "Error al obtener las transacciones: ${exception.message}")
            }
    }
}
