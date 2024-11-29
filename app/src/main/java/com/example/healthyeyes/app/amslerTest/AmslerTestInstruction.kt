package com.example.healthyeyes.app.amslerTest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R
import com.example.healthyeyes.MainViewApp

/**
 * Activity instructing the user how to perform the Amsler test.
 */
class AmslerTestInstruction : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var startTestButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amsler_test_instruction)
        val userID = intent.getStringExtra("userID")
        backButton = findViewById(R.id.backIntoMainViewApp)
        startTestButton = findViewById(R.id.startAmslerTest)

        backButton.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }
        startTestButton.setOnClickListener {
            val selectedEye = "Lewe Oko"
            Toast.makeText(this, "Rozpoczęcie testu dla: $selectedEye", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AmslerTest::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("selectedEye", selectedEye)
            startActivity(intent)
            finish()
        }
    }
}