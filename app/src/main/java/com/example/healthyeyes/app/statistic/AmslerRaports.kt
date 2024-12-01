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
 * Activity displaying reports on Amsler test results received.
 */
class AmslerRaports : AppCompatActivity() {
    private lateinit var lineChartLeftEyeAmsler: LineChart
    private lateinit var lineChartRightEyeAmsler: LineChart
    private lateinit var avgLeftEyeTextViewAmsler: TextView
    private lateinit var avgRightEyeTextViewAmsler: TextView
    private lateinit var rangeSpinnerAmsler: Spinner
    private lateinit var backButtonAmsler: Button
    private lateinit var analysisButtonAmsler: Button
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    private var selectedRangeAmsler: String = "Wszystkie"
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
        setContentView(R.layout.amsler_raports)
        lineChartLeftEyeAmsler = findViewById(R.id.lineChartLeftEyeAmsler)
        lineChartRightEyeAmsler = findViewById(R.id.lineChartRightEyeAmsler)
        avgLeftEyeTextViewAmsler = findViewById(R.id.avgLeftEyeTextViewAmsler)
        avgRightEyeTextViewAmsler = findViewById(R.id.avgRightEyeTextViewAmsler)
        rangeSpinnerAmsler = findViewById(R.id.rangeSpinnerAmsler)
        backButtonAmsler = findViewById(R.id.backButtonAmsler)
        analysisButtonAmsler = findViewById(R.id.analysisButtonAmsler)
        val ranges = arrayOf("Ostatni tydzień", "Ostatni miesiąc", "Ostatni rok", "Wszystkie")
        rangeSpinnerAmsler.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ranges)
        rangeSpinnerAmsler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRangeAmsler = ranges[position]
                val userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
                if (userId.isNotEmpty()) {
                    fetchAmslerData(userId)
                } else {
                    Toast.makeText(this@AmslerRaports, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
        if (userId.isNotEmpty()) {
            fetchAmslerData(userId)
        } else {
            Toast.makeText(this, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
        }

        backButtonAmsler.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userId)
            startActivity(intent)
        }
        analysisButtonAmsler.setOnClickListener {
            navigateToAnalysis()
        }
    }

    /**
     * Fetches the Amsler test data for the user from Firestore based on the selected range.
     *
     * @param userId The user ID (email) to fetch data for.
     */
    private fun fetchAmslerData(userId: String) {
        db.collection("amslerTest")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val amslerData = mutableListOf<SnellenDatabase>()
                for (document in result) {
                    val entry = document.toObject(SnellenDatabase::class.java)
                    amslerData.add(entry)
                }
                if (amslerData.isNotEmpty()) {
                    processAndDisplayData(amslerData)
                } else {
                    Toast.makeText(this, "Brak wyników testu Amslera.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Processes, filters based on selected range and displays Amsler test data in graphs.
     *
     * @param amslerData List of Amsler test results to process.
     */
    private fun processAndDisplayData(amslerData: List<SnellenDatabase>) {
        val filteredData = filterDataByRange(amslerData, selectedRangeAmsler)
        val leftEyeEntries = mutableListOf<Entry>()
        val rightEyeEntries = mutableListOf<Entry>()
        val sortedData = filteredData.sortedBy { dateFormat.parse(it.date) }
        val dates = sortedData.map { it.date }
        val leftEyeResults = sortedData.map { if (it.leftEye == "NIE") 1f else 0f }
        val rightEyeResults = sortedData.map { if (it.rightEye == "NIE") 1f else 0f }
        avgLeftEye = if (leftEyeResults.isNotEmpty()) leftEyeResults.average().toFloat() else 0f
        avgRightEye = if (rightEyeResults.isNotEmpty()) rightEyeResults.average().toFloat() else 0f
        avgLeftEyeTextViewAmsler.text = "Średnie wyniki lewego oka: %.2f".format(avgLeftEye)
        avgRightEyeTextViewAmsler.text = "Średnie wyniki prawego oka: %.2f".format(avgRightEye)
        dates.forEachIndexed { index, date ->
            val formattedDate = dateFormat.parse(date)
            if (formattedDate != null) {
                leftEyeEntries.add(Entry(index.toFloat(), leftEyeResults[index]))
                rightEyeEntries.add(Entry(index.toFloat(), rightEyeResults[index]))
            }
        }
        displayChart(lineChartLeftEyeAmsler, leftEyeEntries, "Wyniki lewego oka", dates)
        displayChart(lineChartRightEyeAmsler, rightEyeEntries, "Wyniki prawego oka", dates)
    }

    /**
     * Displays the line chart for the Amsler test results.
     *
     * @param chart The chart to display the data on.
     * @param entries The list of data entries for the chart.
     * @param label The label for the chart.
     * @param dates The list of dates corresponding to the entries.
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
        chart.axisLeft.apply {
            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.axisMaximum = 1.2f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 1f) "TAK" else if (value == 0f) "NIE" else ""
                }
            }
        }
        chart.axisRight.isEnabled = false
        chart.description.text = "Data testu"
        chart.invalidate()
    }

    /**
     * Filters the Amsler test data based on the selected time range.
     *
     * @param data The list of Amsler test results to filter.
     * @param range The selected time range.
     * @return The filtered list of Amsler test results.
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
     * Function to move to the results analysis activity.
     */
    private fun navigateToAnalysis() {
        val intent = Intent(this, AnalysisChat::class.java)
        val roundedavgLeftEye = String.format("%.2f", avgLeftEye)
        val roundedavgRightEye = String.format("%.2f", avgRightEye)
        val question = "Czy otrzymane średnie wyniki po wykonaniu testu Amslera dla lewego oka:$roundedavgLeftEye oraz dla prawego oka: $roundedavgRightEye są prawidłowe?"
        intent.putExtra("userID", userId)
        intent.putExtra("question", "$question Czy może wskazują na konieczność skonsultowania się z okulistą w celu dokonania dokładniejszych badań?")
        startActivity(intent)
    }
}
