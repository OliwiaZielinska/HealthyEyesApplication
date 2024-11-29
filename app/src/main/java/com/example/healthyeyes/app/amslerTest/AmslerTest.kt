package com.example.healthyeyes.app.amslerTest

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.MainViewApp
import com.example.healthyeyes.R
import com.example.healthyeyes.app.cloudFirestore.AmslerDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Class used for the Amsler test, which is used to test the field of vision.
*/
class AmslerTest : AppCompatActivity() {
    private var leftEyeResult: String? = null
    private var rightEyeResult: String? = null
    private var currentEye: String? = null
    private var userID: String = ""
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amsler_test)
        userID = intent.getStringExtra("userID").toString()
        yesButton = findViewById(R.id.yes_button)
        noButton = findViewById(R.id.no_button)
        saveButton = findViewById(R.id.save_button)
        backButton = findViewById(R.id.snellenTestBackButton)
        saveButton.visibility = Button.INVISIBLE
        backButton.visibility = Button.INVISIBLE
        currentEye = intent.getStringExtra("selectedEye") ?: "Nie wybrano oka"

        yesButton.setOnClickListener {
            saveResult("TAK")
        }
        noButton.setOnClickListener {
            saveResult("NIE")
        }
        saveButton.setOnClickListener {
            Toast.makeText(this, "Wyniki zapisane!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AmslerTestInstruction::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }
        backButton.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Function that records the test result for the eye currently being tested.
     *
     * @param result Test result.
     */
    private fun saveResult(result: String) {
        when (currentEye) {
            "Lewe Oko" -> {
                leftEyeResult = result
                Toast.makeText(this, "Zapisano wynik dla lewego oka: $result", Toast.LENGTH_SHORT).show()
                navigateToNextStep("Prawe Oko", userID, leftEyeResult.toString())
            }
            "Prawe Oko" -> {
                rightEyeResult = result
                Toast.makeText(this, "Zapisano wynik dla prawego oka: $result", Toast.LENGTH_SHORT).show()
                showFinalButtons()
            }
            else -> {
                Toast.makeText(this, "Nieznane oko!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function to proceed to the next step of the test, i.e. the examination of the next eye.
     *
     * @param nextEye Name of the next eye to be tested.
     * @param userID User ID.
     * @param leftEyeResult Test result for the left eye.
     */
    private fun navigateToNextStep(nextEye: String, userID: String, leftEyeResult: String) {
        val intent = Intent(this, AmslerTestBetween::class.java)
        intent.putExtra("userID", userID)
        intent.putExtra("selectedEye", nextEye)
        intent.putExtra("leftEyeResult", leftEyeResult)
        startActivity(intent)
    }

    /**
     * Shows the final buttons ‘ZAPISZ’ and ‘POWRÓT’ and saves the test results in the database.
     */
    private fun showFinalButtons() {
        findViewById<Button>(R.id.save_button).visibility = Button.VISIBLE
        findViewById<Button>(R.id.snellenTestBackButton).visibility = Button.VISIBLE

        val leftResult = intent.getStringExtra("leftEyeResult")
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val amslerDatabase = AmslerDatabase(
            userID,
            currentDate.toString(),
            currentTime,
            leftResult.toString(),
            rightEyeResult.toString(),
        )
        GlobalScope.launch(Dispatchers.Main) {
            val collectionRef = FirebaseFirestore.getInstance().collection("amslerTest")
            val documentRef = collectionRef.document()

            documentRef.set(amslerDatabase)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Dokument dodany z ID: ${documentRef.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Wystąpił błąd", e)
                }
        }
    }
}
