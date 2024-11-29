package com.example.healthyeyes
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.app.registrationQuestion.ActivityRegistationQuestion1

/**
 * An activity that allows the user to go to either the login or registration window.
 */
class MainActivityRegistration1 : AppCompatActivity(){
    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration1)

        val startTestButton1 = findViewById<Button>(R.id.startTestButton1)
        val backButton1 = findViewById<Button>(R.id.backButton1)

        startTestButton1.setOnClickListener {
            val intent = Intent(this, ActivityRegistationQuestion1::class.java)
            startActivity(intent)
        }
        backButton1.setOnClickListener {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
        }
    }
}