package com.example.healthyeyes.app.snellenTest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R
import com.example.healthyeyes.MainViewApp

/**
 * Activity instructing the user how they should perform the Snellen test.
 */
class SnellenTestInstruction : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var startTestButton: Button
    private lateinit var leftEyeRadio: RadioButton
    private lateinit var rightEyeRadio: RadioButton
    private var userID: String = ""
    private var selectedEye: String = ""

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snellen_test_instruction)

        userID = intent.getStringExtra("userID").toString()
        backButton = findViewById(R.id.backIntoMainViewApp)
        startTestButton = findViewById(R.id.startSnellenTest)
        leftEyeRadio = findViewById(R.id.leftEyeRadio)
        rightEyeRadio = findViewById(R.id.rightEyeRadio)

        backButton.setOnClickListener {
            navigateToMainViewApp()
        }
        startTestButton.setOnClickListener {
            selectedEye = when {
                leftEyeRadio.isChecked -> "Lewe Oko"
                rightEyeRadio.isChecked -> "Prawe Oko"
                else -> "Nie wybrano oka"
            }
            if (selectedEye != "Nie wybrano oka") {
                Toast.makeText(this, "Rozpoczęcie testu dla: $selectedEye", Toast.LENGTH_SHORT).show()
                navigateToSnellenTest()
            } else {
                Toast.makeText(this, "Wybierz oko, które będzie teraz badane", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /**
     * A function that takes you to the SnellenTest class when you want to perform a test.
     */
    private fun navigateToSnellenTest() {
        val intent = Intent(this, SnellenTest::class.java)
        intent.putExtra("selectedEye", selectedEye)
        intent.putExtra("userID", userID)
        startActivity(intent)
        finish()
    }
    /**
     * A function that takes the user to the MainViewApp class, to the main application window.
     */
    private fun navigateToMainViewApp() {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
        finish()
    }
}