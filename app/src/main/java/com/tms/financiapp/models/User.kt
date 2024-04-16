package com.tms.financiapp.models

data class User(
    val id: String,
    val idType: Int,
    val email: String,
    val idNumber: String,
    val bankAccounts: List<BankAccount>,
    val transactions: List<Transaction>,
    val name: String
){
    constructor() : this("", 0, "", "", emptyList(), emptyList(), "")

    constructor(
        id: String,
        idType: Int,
        idNumber: String,
        name: String
    ) : this(
        id,
        idType,
        "",
        idNumber,
        emptyList(),
        emptyList(),
        name
    )
}
