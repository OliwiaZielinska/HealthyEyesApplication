package com.example.healthyeyes.app.amslerTest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R

class AmslerTestBetween : AppCompatActivity() {
    private lateinit var nextButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amsler_test_between)
        nextButton = findViewById(R.id.nextStep)
        val userID = intent.getStringExtra("userID")
        val nextEye = intent.getStringExtra("selectedEye") ?: "Prawe Oko"
        val leftEyeResult = intent.getStringExtra("leftEyeResult")

        nextButton.setOnClickListener {
            val intent = Intent(this, AmslerTest::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("selectedEye", nextEye)
            intent.putExtra("leftEyeResult", leftEyeResult)
            startActivity(intent)
        }
    }
}
