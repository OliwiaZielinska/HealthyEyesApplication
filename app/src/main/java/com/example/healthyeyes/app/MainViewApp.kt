package com.example.healthyeyes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.app.amslerTest.AmslerTestInstruction
import com.example.healthyeyes.app.ishiharaTest.IshiharaTestInstruction
import com.example.healthyeyes.app.snellenTest.SnellenTestInstruction
import com.example.healthyeyes.app.statistic.ChooseStatistic

/**
 * A class that allows the user to perform various eye tests and manage settings and log out of
 * the application.
 */
class MainViewApp : AppCompatActivity() {
    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        val userID = intent.getStringExtra("userID")
        val visualAcuityButton = findViewById<Button>(R.id.visualAcuityButton)
        val fieldOfViewButton = findViewById<Button>(R.id.fieldOfViewButton)
        val colorButton = findViewById<Button>(R.id.colorButton)
        val resultsButton = findViewById<Button>(R.id.resultsButton)
        val settingButton = findViewById<Button>(R.id.settingButton)
        val logOutButton = findViewById<Button>(R.id.logOutButton)
        val mapsButton = findViewById<Button>(R.id.mapsButton)

        logOutButton.setOnClickListener {
            val intent = Intent(this, MainActivityLogin::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
            finish()
        }
        visualAcuityButton.setOnClickListener {
            val intent = Intent(this, SnellenTestInstruction::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        fieldOfViewButton.setOnClickListener {
            val intent = Intent(this, AmslerTestInstruction::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        colorButton.setOnClickListener {
            val intent = Intent(this, IshiharaTestInstruction::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        settingButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        resultsButton.setOnClickListener {
            val intent = Intent(this, ChooseStatistic::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
        mapsButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
    }
}