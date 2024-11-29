package com.example.healthyeyes.app.registrationQuestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R

/**
 * Activity on the second question during registration.
 */
class ActivityRegistationQuestion2 : AppCompatActivity() {
    private lateinit var question2YesButton: Button
    private lateinit var question2NoButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_question2)

        var yesCount = intent.getIntExtra("YES_COUNT", 0)
        var noCount = intent.getIntExtra("NO_COUNT", 0)
        question2YesButton = findViewById(R.id.question2Yes)
        question2NoButton = findViewById(R.id.question2No)

        question2NoButton.setOnClickListener {
            noCount++
            val intent = Intent(this, ActivityRegistationQuestion3::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
        question2YesButton.setOnClickListener {
            yesCount++
            val intent = Intent(this, ActivityRegistationQuestion3::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
    }
}