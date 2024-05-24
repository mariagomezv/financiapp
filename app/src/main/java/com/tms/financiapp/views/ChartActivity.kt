package com.tms.financiapp.views

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.tms.financiapp.R
import com.tms.financiapp.controllers.TransactionController
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class ChartActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getTransactions()

        val startDateButton: Button = findViewById(R.id.startDateButton)
        val endDateButton: Button = findViewById(R.id.endDateButton)
        val generateBarChartButton: Button = findViewById(R.id.generateBarChartButton)

        startDateButton.setOnClickListener { showDatePickerDialog(true) }
        endDateButton.setOnClickListener { showDatePickerDialog(false) }
        generateBarChartButton.setOnClickListener { generateBarChart() }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            if (isStartDate) {
                startDate = selectedDate.time
            } else {
                endDate = selectedDate.time
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun getTransactions() {
        val userId = Helper().getUserID()
        val transactionController = TransactionController()
        transactionController.getTransactions(userId) { transactions ->
            generarGraficaPie(transactions)
        }
    }

    private fun generarGraficaPie(transactions: List<Transaction>) {
        val filteredTransactions = transactions.filter {
            it.transactionType == 2 || it.transactionType == 3
        }

        val sumByCategory = filteredTransactions.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = sumByCategory.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val dataSet = PieDataSet(entries, "Gastos")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun generateBarChart() {
        if (startDate != null && endDate != null) {
            val userId = Helper().getUserID()
            val transactionController = TransactionController()
            transactionController.getTransactions(userId) { transactions ->
                val filteredTransactions = transactions.filter {
                    val transactionDate = dateFormat.parse(it.date)
                    transactionDate != null && !transactionDate.before(startDate) && !transactionDate.after(endDate)
                }

                val totalGastos = filteredTransactions.filter { it.transactionType == 2 }.sumOf { it.amount }
                val totalIngresos = filteredTransactions.filter { it.transactionType == 1 }.sumOf { it.amount }
                val totalTransferencias = filteredTransactions.filter { it.transactionType == 3 }.sumOf { it.amount }

                val entries = listOf(
                    BarEntry(0f, totalGastos.toFloat()),
                    BarEntry(1f, totalIngresos.toFloat()),
                    BarEntry(2f, totalTransferencias.toFloat())
                )

                val labels = listOf("Gastos", "Ingresos", "Transferencias")
                val barChart = findViewById<BarChart>(R.id.barChart)
                val dataSet = BarDataSet(entries, "Transacciones")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                val data = BarData(dataSet)
                barChart.data = data
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                barChart.xAxis.granularity = 1f
                barChart.invalidate()
            }
        }
    }
}