package com.example.healthyeyes.app.registrationQuestion
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.ActivityMain
import com.example.healthyeyes.R
import com.example.healthyeyes.app.registration.MainActivityRegistration1

/**
 * An activity displayed to a user with good eyesight, allowing them to either go to
 * the registration window or return to the beginning.
 */
class ActivityRegistationGoodEyesight : AppCompatActivity(){
    private lateinit var backIntoActivityMain: Button
    private lateinit var registrationActivity: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_good)

        backIntoActivityMain = findViewById(R.id.backIntoActivityMain)
        registrationActivity = findViewById(R.id.registrationActivity)

        backIntoActivityMain.setOnClickListener {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
        }
        registrationActivity.setOnClickListener {
            val intent = Intent(this, MainActivityRegistration1::class.java)
            startActivity(intent)
        }
    }
}