package com.example.healthyeyes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Class that allows the user to choose between registration and login.
 */
class ActivityMain : AppCompatActivity() {
    private lateinit var logInButtonEnd: Button
    private lateinit var signUpButton1: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logInButtonEnd = findViewById(R.id.logInButtonEnd)
        signUpButton1 = findViewById(R.id.signUpButton1)

        signUpButton1.setOnClickListener {
            val intent = Intent(this, MainActivityRegistration1::class.java)
            startActivity(intent)
        }
        logInButtonEnd.setOnClickListener {
            val intent = Intent(this, MainActivityLogin::class.java)
            startActivity(intent)
        }
    }
}