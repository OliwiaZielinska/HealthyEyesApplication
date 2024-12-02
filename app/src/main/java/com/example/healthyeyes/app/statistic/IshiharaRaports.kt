package com.example.healthyeyes.app.statistic

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.MainViewApp
import com.example.healthyeyes.R
import com.example.healthyeyes.app.cloudFirestore.IshiharaDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
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
 * A class to visualise the Ishihara test results obtained.
 */
class IshiharaRaports : AppCompatActivity() {
    private lateinit var lineChartIshihara: LineChart
    private lateinit var avgEyesTextView: TextView
    private lateinit var rangeSpinnerIshihara: Spinner
    private lateinit var analysisButtonIshihara: Button
    private lateinit var backButtonIshihara: Button
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    private var selectedRangeIshihara: String = "Wszystkie"
    private var averageResult: Float = 0f
    private var userId: String = ""

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ishihara_raports)

        lineChartIshihara = findViewById(R.id.lineChartIshihara)
        avgEyesTextView = findViewById(R.id.avgEyesTextView)
        rangeSpinnerIshihara = findViewById(R.id.rangeSpinnerIshihara)
        backButtonIshihara = findViewById(R.id.backButtonIshihara)
        analysisButtonIshihara = findViewById(R.id.analysisButtonIshihara)
        val ranges = arrayOf("Ostatni tydzień", "Ostatni miesiąc", "Ostatni rok", "Wszystkie")
        rangeSpinnerIshihara.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ranges)
        rangeSpinnerIshihara.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRangeIshihara = ranges[position]
                val userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
                if (userId.isNotEmpty()) {
                    fetchIshiharaData(userId)
                } else {
                    Toast.makeText(this@IshiharaRaports, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
        if (userId.isNotEmpty()) {
            fetchIshiharaData(userId)
        } else {
            Toast.makeText(this, "Błąd: Nie udało się pobrać danych użytkownika.", Toast.LENGTH_SHORT).show()
        }

        backButtonIshihara.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userId)
            startActivity(intent)
        }
        analysisButtonIshihara.setOnClickListener {
            navigateToAnalysis()
        }
    }

    /**
     * Method to retrieve Ishihara test data from the Firestore for a specific user.
     *
     * @param userId Identifier of the user for whom the data is retrieved.
     */
    private fun fetchIshiharaData(userId: String) {
        db.collection("ishiharaTest")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ishiharaData = mutableListOf<IshiharaDatabase>()
                for (document in result) {
                    val entry = document.toObject(IshiharaDatabase::class.java)
                    ishiharaData.add(entry)
                }
                if (ishiharaData.isNotEmpty()) {
                    processAndDisplayData(ishiharaData)
                } else {
                    Toast.makeText(this, "Brak wyników testu Ishihary.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Processes the Ishihara test data, filters it according to the selected time range and
     * displays a graph.
     *
     * @param ishiharaData List of Ishihara test results to process.
     */
    private fun processAndDisplayData(ishiharaData: List<IshiharaDatabase>) {
        val filteredData = filterDataByRange(ishiharaData, selectedRangeIshihara)
        val sortedData = filteredData.sortedBy { dateFormat.parse(it.date) }
        val entries = mutableListOf<Entry>()
        val dates = sortedData.map { it.date }
        var totalResult = 0f
        sortedData.forEachIndexed { index, data ->
            val normalizedResult = data.result.toFloat() / 25f
            entries.add(Entry(index.toFloat(), normalizedResult))
            totalResult += normalizedResult
        }
        averageResult = if (sortedData.isNotEmpty()) totalResult / sortedData.size * 100 else 0f
        avgEyesTextView.text = "Średni wynik testu: %.2f".format(averageResult)
        displayChart(entries, dates)
    }

    /**
     * Filters data based on the selected time range.
     *
     * @param data List of Ishihara test results.
     * @param range Selected time range to filter.
     * @return Sorted list of results based on range.
     */
    private fun filterDataByRange(data: List<IshiharaDatabase>, range: String): List<IshiharaDatabase> {
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
     * Displays a graph based on the processed data.
     *
     * @param entries List of entries (points) on the graph.
     * @param dates List of dates to be displayed on the X axis.
     */
    private fun displayChart(entries: List<Entry>, dates: List<String>) {
        val dataSet = LineDataSet(entries, "Wyniki testu Ishihary").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
        }
        val lineData = LineData(dataSet)
        lineChartIshihara.data = lineData
        val limitLine = LimitLine(0.68f, "Granica daltonizmu").apply {
            lineWidth = 2f
            lineColor = Color.RED
            textColor = Color.RED
            textSize = 12f
        }
        lineChartIshihara.axisLeft.addLimitLine(limitLine)
        val xAxis = lineChartIshihara.xAxis
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
        lineChartIshihara.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 1f
        }
        lineChartIshihara.axisRight.isEnabled = false
        lineChartIshihara.description.isEnabled = false
        lineChartIshihara.invalidate()
    }

    /**
     * Function to move to the results analysis activity.
     */
    private fun navigateToAnalysis() {
        val intent = Intent(this, AnalysisChat::class.java)
        val roundedAverageResult = String.format("%.2f", averageResult)
        val question = "Jak interpretować wynik testu Ishihara, jeśli użytkownik uzyskał $roundedAverageResult% poprawnych odpowiedzi?"
        intent.putExtra("userID", userId)
        intent.putExtra("question", "$question Czy może wskazują na konieczność skonsultowania się z okulistą w celu dokonania dokładniejszych badań?")
        startActivity(intent)
    }
}
