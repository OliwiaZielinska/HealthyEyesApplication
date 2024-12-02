package com.example.healthyeyes.app.statistic

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.MainViewApp
import com.example.healthyeyes.R
import com.example.healthyeyes.app.cloudFirestore.SnellenDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class for visualising the results of Snellen tests.
 */
class SnellenRaports : AppCompatActivity() {
    private lateinit var lineChartLeftEye: LineChart
    private lateinit var lineChartRightEye: LineChart
    private lateinit var avgLeftEyeTextView: TextView
    private lateinit var avgRightEyeTextView: TextView
    private lateinit var rangeSpinner: Spinner
    private lateinit var backButton: Button
    private lateinit var analysisButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    private var selectedRange: String = "Wszystkie"
    private var avgLeftEye: Float = 0f
    private var avgRightEye: Float = 0f
    private var userId: String = ""

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snellen_raports)

        lineChartLeftEye = findViewById(R.id.lineChartLeftEye)
        lineChartRightEye = findViewById(R.id.lineChartRightEye)
        avgLeftEyeTextView = findViewById(R.id.avgLeftEyeTextView)
        avgRightEyeTextView = findViewById(R.id.avgRightEyeTextView)
        rangeSpinner = findViewById(R.id.rangeSpinner)
        backButton = findViewById(R.id.backButton)
        analysisButton = findViewById(R.id.analysisButton)
        val ranges = arrayOf("Ostatni tydzień", "Ostatni miesiąc", "Ostatni rok", "Wszystkie")
        rangeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ranges)

        rangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRange = ranges[position]
                userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
                if (userId.isNotEmpty()) {
                    fetchSnellenData(userId)
                } else {
                    Toast.makeText(this@SnellenRaports, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
        if (userId.isNotEmpty()) {
            fetchSnellenData(userId)
        } else {
            Toast.makeText(this, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
        }
        backButton.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userId)
            startActivity(intent)
        }
        analysisButton.setOnClickListener {
            navigateToAnalysis()
        }
    }

    /**
     * Function to retrieve Snellen test data from the Firestore for a specific user.
     *
     * @param userId Identifier of the user for whom data is retrieved.
     */
    private fun fetchSnellenData(userId: String) {
        db.collection("snellenTest")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val snellenData = mutableListOf<SnellenDatabase>()

                for (document in result) {
                    val entry = document.toObject(SnellenDatabase::class.java)
                    snellenData.add(entry)
                }
                if (snellenData.isNotEmpty()) {
                    processAndDisplayData(snellenData)
                } else {
                    Toast.makeText(this, "Brak wyników testu Snellena.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * A function that processes the Snellen test results obtained.
     *
     * @param snellenData List of Snellen test results.
     */
    private fun processAndDisplayData(snellenData: List<SnellenDatabase>) {
        val filteredData = filterDataByRange(snellenData, selectedRange)
        val leftEyeEntries = mutableListOf<Entry>()
        val rightEyeEntries = mutableListOf<Entry>()
        val sortedData = filteredData.sortedBy { dateFormat.parse(it.date) }
        val dates = sortedData.map { it.date }
        val leftEyeResults = sortedData.map { it.leftEye.toFloatOrNull() ?: 0f }
        val rightEyeResults = sortedData.map { it.rightEye.toFloatOrNull() ?: 0f }

        avgLeftEye = if (leftEyeResults.isNotEmpty()) leftEyeResults.average().toFloat() else 0f
        avgRightEye = if (rightEyeResults.isNotEmpty()) rightEyeResults.average().toFloat() else 0f
        avgLeftEyeTextView.text = "Średnie wyniki lewego oka: %.2f".format(avgLeftEye)
        avgRightEyeTextView.text = "Średnie wyniki prawego oka: %.2f".format(avgRightEye)

        dates.forEachIndexed { index, date ->
            val formattedDate = dateFormat.parse(date)
            if (formattedDate != null) {
                leftEyeEntries.add(Entry(index.toFloat(), leftEyeResults[index]))
                rightEyeEntries.add(Entry(index.toFloat(), rightEyeResults[index]))
            }
        }
        displayChart(lineChartLeftEye, leftEyeEntries, "Wyniki lewego oka", dates)
        displayChart(lineChartRightEye, rightEyeEntries, "Wyniki prawego oka", dates)
    }

    /**
     * Filters data based on the selected time range.
     *
     * @param data List of Snellen test results.
     * @param range Selected time range to filter.
     * @return Sorted list of results based on range.
     */
    private fun filterDataByRange(data: List<SnellenDatabase>, range: String): List<SnellenDatabase> {
        val now = Calendar.getInstance()
        return when (range) {
            "Ostatni tydzień" -> {
                val oneWeekAgo = now.apply { add(Calendar.DAY_OF_YEAR, -7) }.time
                data.filter { dateFormat.parse(it.date)?.after(oneWeekAgo) == true }
            }
            "Ostatni miesiąc" -> {
                val oneMonthAgo = now.apply { add(Calendar.MONTH, -1) }.time
                data.filter { dateFormat.parse(it.date)?.after(oneMonthAgo) == true }
            }
            "Ostatni rok" -> {
                val oneYearAgo = now.apply { add(Calendar.YEAR, -1) }.time
                data.filter { dateFormat.parse(it.date)?.after(oneYearAgo) == true }
            }
            else -> data
        }
    }

    /**
     * Function to display a graph based on the processed data.
     *
     * @param chart The chart on which the data is to be displayed.
     * @param entries List of entries (points) in the chart.
     * @param label Label for the chart.
     * @param dates List of dates to be displayed on the X axis.
     */
    private fun displayChart(chart: LineChart, entries: List<Entry>, label: String, dates: List<String>) {
        val dataSet = LineDataSet(entries, label).apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 4f
            circleRadius = 6f
            setDrawValues(false)
        }
        val lineData = LineData(dataSet)
        chart.data = lineData
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in dates.indices) {
                    displayFormat.format(dateFormat.parse(dates[index])!!)
                } else {
                    ""
                }
            }
        }
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -30f
        xAxis.textSize = 14f
        chart.axisLeft.apply {
            axisMaximum = 2.5f
            axisMinimum = 0f
            textSize = 14f
        }
        chart.axisRight.isEnabled = false
        chart.description.text = "Data testu"
        chart.description.textSize = 16f
        chart.setExtraOffsets(10f, 10f, 10f, 20f)
        chart.invalidate()
    }

    /**
     * Function to move to the results analysis activity.
     */
    private fun navigateToAnalysis() {
        val intent = Intent(this, AnalysisChat::class.java)
        val roundedavgLeftEye = String.format("%.2f", avgLeftEye)
        val roundedavgRightEye = String.format("%.2f", avgRightEye)
        val question = "Czy wyniki testu Snellena zapisane jako wartości dziesiętne ($roundedavgLeftEye dla lewego oka i $roundedavgRightEye dla prawego oka) mieszczą się w normie i czy wymagają konsultacji z okulistą?"
        intent.putExtra("userID", userId)
        intent.putExtra("question", "$question Co oznaczają takie wartości w odniesieniu do standardowego wzroku 20/20?")
        startActivity(intent)
    }
}
