package com.tms.financiapp.models

import com.tms.financiapp.models.enums.TransactionType

data class Transaction(
    val id: Int,
    val userId: String,
    val transactionType: Int,
    val amount: Double,
    val date: String,
    val description: String,
    val account: String,
    val toAccount: String
){
    constructor(): this(1, "", 0, 0.0, "", "", "", "")
}
