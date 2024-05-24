package com.tms.financiapp.models

import android.os.Parcel
import android.os.Parcelable

data class Transaction(
    val id: Int,
    val userId: String,
    val transactionType: Int,
    val amount: Double,
    val date: String,
    val category: String,
    val description: String,
    val account: String,
    val toAccount: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )
    constructor(): this(1, "", 0, 0.0, "", "", "", "", "")
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userId)
        parcel.writeInt(transactionType)
        parcel.writeDouble(amount)
        parcel.writeString(date)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(account)
        parcel.writeString(toAccount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }

}