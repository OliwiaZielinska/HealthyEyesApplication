package com.example.healthyeyes.app.registrationQuestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.app.registration.MainActivityRegistration1
import com.example.healthyeyes.R

/**
 * Activity on the last - fourth - question at registration.
 */
class ActivityRegistationQuestion4 : AppCompatActivity() {
    private lateinit var question4YesButton: Button
    private lateinit var question4NoButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_question4)

        var yesCount = intent.getIntExtra("YES_COUNT", 0)
        var noCount = intent.getIntExtra("NO_COUNT", 0)
        question4YesButton = findViewById(R.id.question4Yes)
        question4NoButton = findViewById(R.id.question4No)

        question4NoButton.setOnClickListener {
            noCount++
            checkAnswerAndNavigate(yesCount, noCount)
        }
        question4YesButton.setOnClickListener {
            yesCount++
            checkAnswerAndNavigate(yesCount, noCount)
        }
    }

    /**
     * Function that decides to navigate to the appropriate activity based on the number
     * of ‘Yes’ responses.
     *
     * @param yesCount number of ‘Yes’ responses given by the user.
     * @param noCount number of ‘No’ answers given by the user.
     */
    private fun checkAnswerAndNavigate(yesCount: Int, noCount: Int) {
        val intent = if (yesCount >= 2) {
            Intent(this, MainActivityRegistration1::class.java)
        } else {
            Intent(this, ActivityRegistationGoodEyesight::class.java)
        }
        intent.putExtra("YES_COUNT", yesCount)
        intent.putExtra("NO_COUNT", noCount)
        startActivity(intent)
    }
}
