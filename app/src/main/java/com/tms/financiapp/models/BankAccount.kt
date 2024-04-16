package com.tms.financiapp.models

data class BankAccount(
    val userId: String,
    val accountNumber: String,
    val accountType: Int,
    val balance: Double,
    val openDateAccount: String,
    val isActive: Boolean
)
{
    constructor() : this("", "", 0, 0.0, "", false
    )
}
