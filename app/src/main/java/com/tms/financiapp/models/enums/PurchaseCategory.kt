package com.tms.financiapp.models.enums

enum class PurchaseCategory(val code: Int, val displayName: String) {
    GROCERIES(1, "Groceries"),
    TRANSPORTATION(2, "Transportation"),
    ENTERTAINMENT(3, "Entertainment"),
    UTILITIES(4, "Utilities"),
    OTHER(5, "Other")
}