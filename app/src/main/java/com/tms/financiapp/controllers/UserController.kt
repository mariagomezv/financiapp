package com.tms.financiapp.controllers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tms.financiapp.helpers.UserNotFoundException
import com.tms.financiapp.models.BankAccount
import com.tms.financiapp.models.Transaction
import com.tms.financiapp.models.User

class UserController {
    private val db = FirebaseFirestore.getInstance()

    // Get the user document for the given ID
    private fun getUserDocument(userId: String): DocumentReference {
        return db.collection("users").document(userId)
    }

    // Create a new user with the specified ID
    fun createUser(userId: String, email: String) {
        getUserDocument(userId).set(
            User(
                id = userId,
                name = "",
                idType = 0,
                idNumber = "",
                email = email,
                bankAccounts = emptyList(),
                transactions = emptyList()
            )
        )
    }

    // Get the user document for the given ID
    fun getUser(userId: String, callback: (User) -> Unit) {
        getUserDocument(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                callback(user!!)
            } else {
                throw UserNotFoundException()
            }
        }
    }

    // Update the user's ID information
    fun updateUser(userId: String, user: User) {
        getUserDocument(userId).update(
            mapOf(
                "name" to user.name,
                "idType" to user.idType,
                "idNumber" to user.idNumber
            )
        )
    }

    // Add a new bank account to the user's document
    fun addBankAccount(userId: String, bankAccount: BankAccount) {
        getUserDocument(userId).update(
            "bankAccounts",
            FieldValue.arrayUnion(bankAccount)
        )
    }

    // Get the bank accounts for the user
    fun getBankAccounts(userId: String, callback: (List<BankAccount>) -> Unit) {
        getUserDocument(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                callback(user?.bankAccounts?.toList() ?: emptyList())
            } else {
                callback(emptyList())
            }
        }
    }

    // Add a new transaction to the user's document
    fun addTransaction(transaction: Transaction) {
        getUserDocument(transaction.userId).update(
            "transactions",
            FieldValue.arrayUnion(transaction)
        )
    }

    // Get the transactions for the user
    fun getTransactions(userId: String, callback: (List<Transaction>) -> Unit) {
        getUserDocument(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                callback(user?.transactions?.toList() ?: emptyList())
            } else {
                callback(emptyList())
            }
        }
    }

    /*private val connectionDB = ConnectionDB()
    fun createUser(user: User): User {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = connectionDB.getConnection()
            preparedStatement = connection?.prepareStatement("INSERT INTO users (user_id, id_type, id_number) VALUES (?, ?, ?)")
            preparedStatement?.setString(1, user.userId)
            preparedStatement?.setInt(2, user.idType)
            preparedStatement?.setString(3, user.idNumber)
            preparedStatement?.executeUpdate()
            return user
        } catch (e: Exception) {
            e.printStackTrace()
            return User(0, "", 0, "")
        } finally {
            connectionDB.close(connection)
        }
    }*/
}