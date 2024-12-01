package com.example.healthyeyes.app.statistic

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.MainViewApp
import com.example.healthyeyes.R

/**
 * A class that allows the user to select the test whose statistics they want to see.
 */
class ChooseStatistic : AppCompatActivity() {
    private lateinit var snellenButton: Button
    private lateinit var amsleraButton: Button
    private lateinit var ishiharyButton: Button
    private lateinit var backButton: Button
    private var userID: String = ""

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_statistics)

        userID = intent.getStringExtra("userID").toString()
        snellenButton = findViewById(R.id.snellenButton)
        amsleraButton = findViewById(R.id.amsleraButton)
        ishiharyButton = findViewById(R.id.ishiharyButton)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, MainViewApp::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }

        snellenButton.setOnClickListener {
            val intent = Intent(this, SnellenRaports::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        amsleraButton.setOnClickListener {
            val intent = Intent(this, AmslerRaports::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        ishiharyButton.setOnClickListener {
            val intent = Intent(this, IshiharaRaports::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
    }
}