package com.example.healthyeyes.app.snellenTest

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
 * Activity for conducting the Snellen test, which assesses visual acuity.
 * The test involves displaying rows of letters and verifying user input for correctness.
 */
class SnellenTest : AppCompatActivity() {
    private lateinit var snellenTestHeader: TextView
    private lateinit var userInput: EditText
    private lateinit var checkButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var backButton: Button
    private lateinit var speakButton: Button
    private var level = 1
    private var bestLevel = 0
    private var firstEyeResult = ""
    private var secondEyeResult = ""
    private var currentEye = ""
    private var isFirstEyeTestComplete = false
    private var rightEyeResult = ""
    private var leftEyeResult = ""
    private var userID = ""
    private val allowedLetters = listOf("C", "D", "E", "F", "L", "O", "P", "T", "Z")
    private val snellenScale = mapOf(
        1 to "0.1",
        2 to "0.2",
        3 to "0.3",
        4 to "0.4",
        5 to "0.5",
        6 to "0.6",
        7 to "0.7",
        8 to "0.8",
        9 to "0.9",
        10 to "1.0"
    )

    /**
     * Initializes the activity and sets up listeners for buttons and other UI elements.
     * Also prepares the first row of the Snellen test and notifies the user about the test distance.
     *
     * @param savedInstanceState The saved state of the application.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snellen_test)

        userID = intent.getStringExtra("userID").toString()
        snellenTestHeader = findViewById(R.id.snellenTestHeader)
        userInput = findViewById(R.id.userInput)
        checkButton = findViewById(R.id.checkButton)
        resultTextView = findViewById(R.id.resultText)
        backButton = findViewById(R.id.snellenTestBackButton)
        speakButton = findViewById(R.id.speakButton)
        currentEye = intent.getStringExtra("selectedEye") ?: "Lewe Oko"

        resultTextView.visibility = TextView.GONE
        backButton.visibility = Button.GONE

        speakButton.setOnClickListener { startSpeechRecognition() }
        backButton.setOnClickListener { navigateToSnellenTestInstruction() }
        checkButton.setOnClickListener { processAnswer() }

        updateRow()
        Toast.makeText(this, "Trzymaj urządzenie w odległości 30 cm od oczu.", Toast.LENGTH_LONG).show()
    }

    /**
     * Updates the test display by setting the text size and content of the Snellen row.
     */
    private fun updateRow() {
        val textSize = calculateTextSizeForSnellen(level, resources.displayMetrics.density)
        snellenTestHeader.textSize = textSize
        snellenTestHeader.text = generateRow(5)
    }

    /**
     * Calculates the appropriate text size for a given Snellen test level.
     *
     * @param level The current test level.
     * @param screenDensity The screen density of the device.
     * @return The calculated text size in pixels.
     */
    private fun calculateTextSizeForSnellen(level: Int, screenDensity: Float): Float {
        val physicalSizeInCm = when (level) {
            1 -> 8.7f
            2 -> 6.5f
            3 -> 4.8f
            4 -> 3.7f
            5 -> 2.9f
            6 -> 2.3f
            7 -> 1.8f
            8 -> 1.4f
            9 -> 1.1f
            10 -> 0.8f
            else -> 1.0f
        }
        return physicalSizeInCm * screenDensity * (30f / 25.4f)
    }

    /**
     * Generates a random row of letters for the test.
     *
     * @param lettersCount The number of letters to include in the row.
     * @return A string of random letters.
     */
    private fun generateRow(lettersCount: Int): String {
        return (1..lettersCount)
            .map { allowedLetters[Random.nextInt(allowedLetters.size)] }
            .joinToString(" ")
    }

    /**
     * Processes the user’s input and provides feedback on whether the answer is correct.
     */
    private fun processAnswer() {
        val correctAnswer = snellenTestHeader.text.toString().replace(" ", "").uppercase()
        val userAnswer = userInput.text.toString().replace(" ", "").uppercase()

        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Proszę wpisać odpowiedź!", Toast.LENGTH_SHORT).show()
            return
        }

        if (correctAnswer == userAnswer) {
            Toast.makeText(this, "Poprawna odpowiedź!", Toast.LENGTH_SHORT).show()
            if (level > bestLevel) bestLevel = level
        } else {
            Toast.makeText(this, "Niepoprawna odpowiedź. Spróbuj jeszcze raz!", Toast.LENGTH_SHORT).show()
            userInput.text.clear()
        }
        nextLevel()
    }

    /**
     * Advances the test to the next level or saves the results if the test is complete.
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
     * Saves the result of the current test for the left or right eye and prepares for the next eye if needed.
     */
    private fun saveResult() {
        val snellenResult = snellenScale[bestLevel] ?: "Nieznany"
        if (!isFirstEyeTestComplete) {
            firstEyeResult = snellenResult
            if (currentEye == "Lewe Oko") leftEyeResult = snellenResult else rightEyeResult = snellenResult
            isFirstEyeTestComplete = true
            resetTestForSecondEye()
        } else {
            secondEyeResult = snellenResult
            if (currentEye == "Lewe Oko") leftEyeResult = snellenResult else rightEyeResult = snellenResult
            showFinalResults()
        }
    }

    /**
     * Resets the test for the second eye.
     */
    private fun resetTestForSecondEye() {
        currentEye = if (currentEye == "Lewe Oko") "Prawe Oko" else "Lewe Oko"
        level = 1
        bestLevel = 0
        userInput.text.clear()
        updateRow()
        Toast.makeText(this, "Pora zbadać $currentEye. Zasłoń drugie oko.", Toast.LENGTH_LONG).show()
    }

    /**
     * Displays the final results of the Snellen test for both eyes.
     */
    private fun showFinalResults() {
        val leftEyeText = leftEyeResult.ifEmpty { getString(R.string.unknown) }
        val rightEyeText = rightEyeResult.ifEmpty { getString(R.string.unknown) }
        resultTextView.text = getString(R.string.snellen_result_text, leftEyeText, rightEyeText)
        resultTextView.visibility = TextView.VISIBLE
        checkButton.isEnabled = false
        backButton.visibility = Button.VISIBLE
        saveResultToDatabase()
    }

    /**
     * Saves the Snellen test results to the Firestore database.
     */
    private fun saveResultToDatabase() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val snellenDatabase = SnellenDatabase(userID, currentDate, currentTime, leftEyeResult, rightEyeResult)

        GlobalScope.launch(Dispatchers.Main) {
            val collectionRef = FirebaseFirestore.getInstance().collection("snellenTest")
            collectionRef.document().set(snellenDatabase)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Wynik zapisany") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Błąd zapisu", e) }
        }
    }

    /**
     * Navigates back to the Snellen test instruction screen.
     */
    private fun navigateToSnellenTestInstruction() {
        val intent = Intent(this, SnellenTestInstruction::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
        finish()
    }

    /**
     * Starts the speech recognition process to capture the user’s spoken input.
     */
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Wypowiedz litery, które widzisz")
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Rozpoznawanie mowy nie jest dostępne na tym urządzeniu.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles the result from the speech recognition activity.
     *
     * @param requestCode The request code for the activity.
     * @param resultCode The result code from the activity.
     * @param data The intent data returned by the activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0)?.replace(" ", "")?.uppercase() ?: ""

            if (spokenText.isNotEmpty()) {
                val isInputValid = spokenText.all { it.toString() in allowedLetters }
                if (isInputValid) userInput.setText(spokenText)
                else Toast.makeText(this, "Niedozwolone znaki.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Nie rozpoznano tekstu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_SPEECH_INPUT = 100
    }
}
