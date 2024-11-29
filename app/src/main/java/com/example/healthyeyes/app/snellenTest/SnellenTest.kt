package com.example.healthyeyes.app.snellenTest

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R
import com.example.healthyeyes.app.cloudFirestore.SnellenDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * The class allows the user to perform the Snellen test, which is used to assess visual acuity.
 */
class SnellenTest : AppCompatActivity() {
    private lateinit var snellenRow: TextView
    private lateinit var userInput: EditText
    private lateinit var checkButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var backButton: Button
    private var level = 1
    private var bestLevel = 0
    private var firstEyeResult = ""
    private var secondEyeResult = ""
    private var currentEye = ""
    private var isFirstEyeTestComplete = false
    private var rightEyeResult = ""
    private var leftEyeResult = ""
    private var userID = ""
    private val snellenScale = mapOf(
        1 to "20/200",
        2 to "20/100",
        3 to "20/70",
        4 to "20/50",
        5 to "20/40",
        6 to "20/30",
        7 to "20/25",
        8 to "20/20",
        9 to "20/15",
        10 to "20/10"
    )

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snellen_test)

        userID = intent.getStringExtra("userID").toString()
        snellenRow = findViewById(R.id.snellenRow)
        userInput = findViewById(R.id.userInput)
        checkButton = findViewById(R.id.checkButton)
        resultTextView = findViewById(R.id.resultText)
        backButton = findViewById(R.id.snellenTestBackButton)
        currentEye = intent.getStringExtra("selectedEye") ?: "Lewe Oko"
        resultTextView.visibility = TextView.GONE
        backButton.visibility = Button.GONE

        backButton.setOnClickListener {
            navigateToSnellenTestInstruction()
        }
        updateRow()
        checkButton.setOnClickListener {
            processAnswer()
        }
    }

    /**
     * A function that takes the user to the SnellenTestInstruction class when he or she
     * presses the ‘POWRÓT’ button .
     */
    private fun navigateToSnellenTestInstruction() {
        val intent = Intent(this, SnellenTestInstruction::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
        finish()
    }

    /**
     * * Function that generates a random set of letters based on the number of letters required
     * for the test level.
     *
     * @param lettersCount Number of letters in the generated row.
     * @return The string representing the random row of letters.
     */
    private fun generateRow(lettersCount: Int): String {
        val snellenLetters = listOf("C", "D", "E", "F", "L", "O", "P", "T", "Z")
        return (1..lettersCount)
            .map { snellenLetters[Random.nextInt(snellenLetters.size)] }
            .joinToString(" ")
    }

    /**
     * Function that updates the row of letters on the screen depending on the level of the test.
     */
    private fun updateRow() {
        val textSize = 48 - (level * 4)
        snellenRow.textSize = textSize.toFloat()
        snellenRow.text = generateRow(5)
    }

    /**
     * A function that processes the user's response, in order to check its correctness and
     * allow it to move to the next level of the test.
     */
    private fun processAnswer() {
        val correctAnswer = snellenRow.text.toString().replace(" ", "").uppercase()
        val userAnswer = userInput.text.toString().replace(" ", "").uppercase()

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Proszę wpisać odpowiedź!", Toast.LENGTH_SHORT).show()
            return
        }

        if (correctAnswer == userAnswer) {
            Toast.makeText(this, "Poprawna odpowiedź!", Toast.LENGTH_SHORT).show()
            if (level > bestLevel) {
                bestLevel = level
            }
        } else {
            Toast.makeText(this, "Niepoprawna odpowiedź, przechodzimy dalej.", Toast.LENGTH_SHORT).show()
        }
        nextLevel()
    }

    /**
     * Function to move to the next level of the test or to save the result in the database.
     */
    private fun nextLevel() {
        if (level < 10) {
            level++
            updateRow()
            userInput.text.clear()
        } else {
            saveResult()
        }
    }

    /**
     * Function used to save the test result.
     */
    private fun saveResult() {
        val snellenResult = snellenScale[bestLevel] ?: "Nieznany"

        if (!isFirstEyeTestComplete) {
            firstEyeResult = snellenResult
            if (currentEye == "Lewe Oko") {
                leftEyeResult = snellenResult
            } else if (currentEye == "Prawe Oko") {
                rightEyeResult = snellenResult
            }
            Toast.makeText(this, "Wynik dla pierwszego oka ($currentEye): $firstEyeResult", Toast.LENGTH_LONG).show()
            isFirstEyeTestComplete = true
            resetTestForSecondEye()
        } else {
            secondEyeResult = snellenResult
            Toast.makeText(this, "Wynik dla drugiego oka ($currentEye): $secondEyeResult", Toast.LENGTH_LONG).show()
            if (currentEye == "Lewe Oko") {
                leftEyeResult = snellenResult
            } else if (currentEye == "Prawe Oko") {
                rightEyeResult = snellenResult
            }
            showFinalResults()
        }
    }

    /**
     * Function to reset the test status for the second eye to start from level one.
     */
    private fun resetTestForSecondEye() {
        currentEye = if (currentEye == "Lewe Oko") "Prawe Oko" else "Lewe Oko"
        level = 1
        bestLevel = 0
        userInput.text.clear()
        Toast.makeText(this, "Pora zbadać $currentEye, w związku z tym zasłoń badaną dotychczas gałkę oczną.", Toast.LENGTH_LONG).show()
        updateRow()
    }

    /**
     * Function to display the final results for both eyes and saves them in the database.
     */
    private fun showFinalResults() {
        val leftEyeText = leftEyeResult.ifEmpty { getString(R.string.unknown) }
        val rightEyeText = rightEyeResult.ifEmpty { getString(R.string.unknown) }
        resultTextView.text = getString(R.string.snellen_result_text, leftEyeText, rightEyeText)
        resultTextView.visibility = TextView.VISIBLE
        checkButton.isEnabled = false
        backButton.visibility = Button.VISIBLE
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val snellenDatabase = SnellenDatabase(
            userID,
            currentDate,
            currentTime,
            leftEyeResult,
            rightEyeResult,
        )
        GlobalScope.launch(Dispatchers.Main) {
            val collectionRef = FirebaseFirestore.getInstance().collection("snellenTest")
            val documentRef = collectionRef.document()
            documentRef.set(snellenDatabase)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Dokument dodany z ID: ${documentRef.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Wystąpił błąd", e)
                }
        }
    }
}