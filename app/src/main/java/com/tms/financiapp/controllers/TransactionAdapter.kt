package com.tms.financiapp.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tms.financiapp.R
import com.tms.financiapp.models.Transaction
import com.tms.financiapp.models.enums.TransactionType

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTransactionTitle)
        private val typeTextView: TextView = itemView.findViewById(R.id.textViewTransactionType)
        private val amountTextView: TextView = itemView.findViewById(R.id.textViewTransactionAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewTransactionDate)

        fun bind(transaction: Transaction) {
            titleTextView.text = transaction.description
            amountTextView.text = transaction.amount.toString()
            dateTextView.text = transaction.date

            val transactionTypeText = when (transaction.transactionType) {
                TransactionType.DEPOSIT.code -> TransactionType.DEPOSIT.displayName
                TransactionType.WITHDRAWAL.code -> TransactionType.WITHDRAWAL.displayName
                TransactionType.TRANSFER.code -> TransactionType.TRANSFER.displayName
                else -> "Desconocido"
            }
            typeTextView.text = transactionTypeText
        }
    }
}
