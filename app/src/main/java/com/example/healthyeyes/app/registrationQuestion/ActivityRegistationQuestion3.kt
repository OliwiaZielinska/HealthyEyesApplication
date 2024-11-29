package com.example.healthyeyes.app.registrationQuestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.R

/**
 * Activity on the third question during registration.
 */
class ActivityRegistationQuestion3 : AppCompatActivity() {
    private lateinit var question3YesButton: Button
    private lateinit var question3NoButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_question3)

        var yesCount = intent.getIntExtra("YES_COUNT", 0)
        var noCount = intent.getIntExtra("NO_COUNT", 0)
        question3YesButton = findViewById(R.id.question3Yes)
        question3NoButton = findViewById(R.id.question3No)

        question3NoButton.setOnClickListener {
            noCount++
            val intent = Intent(this, ActivityRegistationQuestion4::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
        question3YesButton.setOnClickListener {
            yesCount++
            val intent = Intent(this, ActivityRegistationQuestion4::class.java)
            intent.putExtra("YES_COUNT", yesCount)
            intent.putExtra("NO_COUNT", noCount)
            startActivity(intent)
        }
    }
}