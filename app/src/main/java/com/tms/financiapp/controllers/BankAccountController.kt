package com.tms.financiapp.controllers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tms.financiapp.models.BankAccount
import com.tms.financiapp.models.User


class BankAccountController {
    private val db = FirebaseFirestore.getInstance()

    // Add a new bank account to a user
    fun addBankAccount(userId: String, bankAccount: BankAccount) {
        val userRef = db.collection("users").document(userId)
        userRef.update("bankAccounts", FieldValue.arrayUnion(bankAccount))
        createBankAccount(bankAccount)
    }

    // Get the bank accounts for a user
    fun getBankAccounts(userId: String, callback: (List<BankAccount>) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user: User? = document.toObject(User::class.java)
                callback(user?.bankAccounts?.toList() ?: emptyList())
            } else {
                callback(emptyList())
            }
        }
    }

    fun fetchAccountsFromFirestore(callback: (List<BankAccount>) -> Unit){
        val accountsCollection = db.collection("accounts")
        accountsCollection.get().addOnSuccessListener { documents ->
            val accountsList = mutableListOf<BankAccount>()
            for (document in documents) {
                // Convertir cada documento a un objeto BankAccount y agregarlo a la lista
                val account = document.toObject(BankAccount::class.java)
                accountsList.add(account)
                callback(accountsList)
            }
        }
    }
    fun verifyAccountExists(accountNumber: String, callback: (Boolean) -> Unit) {
        val accountsCollection = db.collection("accounts")
        accountsCollection.whereEqualTo("accountNumber", accountNumber)
            .get()
            .addOnSuccessListener { documents ->
                val accountExists = !documents.isEmpty()
                callback(accountExists)
            }
    }
    // Update the balance of a bank account
    fun updateBalance(userId: String, accountNumber: String, newBalance: Double) {
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                val updatedBankAccounts = user?.bankAccounts?.map { account: BankAccount ->
                    if (account.accountNumber == accountNumber) {
                        account.copy(balance = newBalance)
                    } else {
                        account
                    }
                }
                userRef.update("bankAccounts", updatedBankAccounts)
            }
        }
    }

    fun updateBalanceInAccounts(accountNumber: String, newBalance: Double) {
        val accountsCollectionRef = db.collection("accounts")
        accountsCollectionRef.whereEqualTo("accountNumber", accountNumber)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Actualiza el saldo en la colección "accounts"
                    accountsCollectionRef.document(document.id).update("balance", newBalance)
                }
            }
    }
    fun getBalanceByAccountNumber(accountNumber: String, callback: (Double?) -> Unit) {
        val accountsCollection = db.collection("accounts")
        accountsCollection.whereEqualTo("accountNumber", accountNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty()) {
                    // No se encontró una cuenta con el número proporcionado
                    callback(null)
                } else {
                    val account = documents.documents.first().toObject(BankAccount::class.java)
                    callback(account?.balance)
                }
            }
            .addOnFailureListener {
                // Error al obtener el balance de la cuenta
                callback(null)
            }
    }
    fun createBankAccount(bankAccount: BankAccount) {
            getUserDocument(bankAccount.accountNumber).set(bankAccount)
        }
    private fun getUserDocument(bankAccountNumber: String): DocumentReference {
        return db.collection("accounts").document(bankAccountNumber)
    }

}




/*    fun generateUniqueAccountNumber(collection: String): String {
        val MAX_REINTENTOS = 10

        for (i in 0 until MAX_REINTENTOS) {
            val numeroCuenta = (0..999999).random() // Genera aleatorio entre 0 y 999999

            val consulta = db.collection(collection)
                .whereEqualTo("accountNumber", numeroCuenta.toString())
                .get()

            await(consulta)

            // Si no hay documentos con el número generado, se retorna
            if (consulta.result?.isEmpty() == true) {
                return numeroCuenta.toString()
            }
        }
        return "0"
    }*/