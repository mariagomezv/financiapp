package com.tms.financiapp.models.enums

enum class AccountType(val code: Int, val displayName: String) {
    CHECKING(1, "Checking"),
    SAVINGS(2, "Savings")
}