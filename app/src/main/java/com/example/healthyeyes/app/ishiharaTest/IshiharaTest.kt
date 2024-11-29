package com.example.healthyeyes.app.ishiharaTest

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R
import com.example.healthyeyes.MainViewApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import com.example.healthyeyes.app.cloudFirestore.IshiharaDatabase

/**
 * A class enabling the Ishihara test, which is used to diagnose colour vision disorders.
 */
class IshiharaTest : AppCompatActivity() {
    private val plates = mutableListOf(
        R.drawable.picture12,
        R.drawable.picture15,
        R.drawable.picture16,
        R.drawable.picture2,
        R.drawable.picture26,
        R.drawable.picture29,
        R.drawable.picture3,
        R.drawable.picture35,
        R.drawable.picture42,
        R.drawable.picture45,
        R.drawable.picture5a,
        R.drawable.picture5,
        R.drawable.picture57,
        R.drawable.picture6a,
        R.drawable.picture6,
        R.drawable.picture7,
        R.drawable.picture73,
        R.drawable.picture74,
        R.drawable.picture8,
        R.drawable.picture96,
        R.drawable.picture97,
        R.drawable.nothing2,
        R.drawable.nothing45,
        R.drawable.nothing5,
        R.drawable.nothing73
    )
    private val answers = mutableListOf("12", "15", "16", "2", "26", "29", "3", "35", "42", "45",
        "5", "5", "57", "6", "6", "7", "73", "74", "8", "96", "97", "0", "0", "0", "0")
    private var currentPlateIndex = 0
    private var score = 0
    private lateinit var imageView: ImageView
    private lateinit var userInput: EditText
    private lateinit var submitButton: Button
    private lateinit var backButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ishihara_test)

        val userID = intent.getStringExtra("userID")
        imageView = findViewById(R.id.ishiharaImage)
        userInput = findViewById(R.id.userInput)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)

        backButton.visibility = Button.GONE

        shufflePlatesAndAnswers()
        userInput.filters = arrayOf(DigitsInputFilter())
        loadPlate(imageView)
        submitButton.setOnClickListener {
            val userAnswer = userInput.text.toString().trim()

            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Proszę wpisać odpowiedź", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userAnswer == answers[currentPlateIndex]) {
                score++
                Toast.makeText(this, "Poprawna odpowiedź!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Niepoprawna odpowiedź. Poprawna odpowiedź to: ${answers[currentPlateIndex]}", Toast.LENGTH_SHORT).show()
            }
            currentPlateIndex++
            if (currentPlateIndex < plates.size) {
                userInput.text.clear()
                loadPlate(imageView)
            } else {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val ishiharaDatabase = IshiharaDatabase(
                    userID.toString(),
                    currentDate,
                    currentTime,
                    score.toString()
                )
                GlobalScope.launch(Dispatchers.Main) {
                    val collectionRef = FirebaseFirestore.getInstance().collection("ishiharaTest")
                    val documentRef = collectionRef.document()

                    documentRef.set(ishiharaDatabase)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Dokument dodany z ID: ${documentRef.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Wystąpił błąd", e)
                        }
                }

                Toast.makeText(this, "Test zakończony! Twój wynik: $score/${plates.size}", Toast.LENGTH_LONG).show()
                endTest(submitButton, backButton, userInput)
            }
        }
        backButton.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Function to load the current Ishihara array into the view.
     *
     * @param imageView Current displayed array.
     */
    private fun loadPlate(imageView: ImageView) {
        imageView.setImageResource(plates[currentPlateIndex])
    }

    /**
     * Function that mixes arrays of images and responses in random order.
     */
    private fun shufflePlatesAndAnswers() {
        val indices = plates.indices.toList().shuffled(Random(System.currentTimeMillis()))
        val shuffledPlates = plates.toMutableList()
        val shuffledAnswers = answers.toMutableList()
        for ((i, index) in indices.withIndex()) {
            plates[i] = shuffledPlates[index]
            answers[i] = shuffledAnswers[index]
        }
    }

    /**
     * Function to disable user interaction after the test.
     *
     * @param submitButton ‘SPRAWDŹ’ button.
     * @param backButton ‘POWRÓT’ button.
     * @param userInput User response text field.
     */
    private fun endTest(submitButton: Button, backButton: Button, userInput: EditText) {
        submitButton.isEnabled = false
        backButton.visibility = Button.VISIBLE
        userInput.isEnabled = false
    }

    /**
     * Input filter limiting character input to digits.
     */
    class DigitsInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (source != null && source.matches(Regex("\\D"))) {
                return ""
            }
            return null
        }
    }
}
