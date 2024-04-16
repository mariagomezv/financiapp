package com.tms.financiapp.models.enums

enum class TransactionType(val code: Int, val displayName: String) {
    DEPOSIT(1, "Deposit"),
    WITHDRAWAL(2, "Withdrawal"),
    TRANSFER(3, "Transfer")
}