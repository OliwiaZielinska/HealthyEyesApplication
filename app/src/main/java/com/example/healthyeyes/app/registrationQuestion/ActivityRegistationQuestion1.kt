package com.example.healthyeyes.app.registrationQuestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R

/**
 * Activity on the first question during registration.
 */
class ActivityRegistationQuestion1 : AppCompatActivity() {
    private lateinit var question1YesButton: Button
    private lateinit var question1NoButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_question1)

        var yesCount = intent.getIntExtra("YES_COUNT", 0)
        var noCount = intent.getIntExtra("NO_COUNT", 0)
        question1YesButton = findViewById(R.id.question1Yes)
        question1NoButton = findViewById(R.id.question1No)

        question1NoButton.setOnClickListener {
            noCount++
            val intent = Intent(this, ActivityRegistationQuestion2::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
        question1YesButton.setOnClickListener {
            yesCount++
            val intent = Intent(this, ActivityRegistationQuestion2::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
    }
}
