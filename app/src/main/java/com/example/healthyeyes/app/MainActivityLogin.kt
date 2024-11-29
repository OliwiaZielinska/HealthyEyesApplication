package com.example.healthyeyes
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.healthyeyes.app.registration.BaseActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * A class that allows a user to log in to an application.
 */
class MainActivityLogin : BaseActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        email = findViewById(R.id.LoginEmailInput)
        password = findViewById(R.id.LoginPasswordInput)
        loginButton = findViewById(R.id.LoginLogInButton)

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    /**
     * Validate login data.
     *
     * @return True if the login data is correct or False when it is wrong.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(email?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Proszę wprowadzić email", true)
                false
            }
            TextUtils.isEmpty(password?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Proszę wprowadzić hasło", true)
                false
            }
            else -> {
                showErrorSnackBar("Wprowadzone dane logowania są poprawne", false)
                true
            }
        }
    }

    /**
     * Login of a registered user.
     */
    private fun loginUser() {
        if (validateLoginDetails()) {
            val emailUser = email?.text.toString().trim { it <= ' ' }
            val passwordUser = password?.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailUser, passwordUser)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar("Zalogowano pomyślnie", false)
                        goToMainActivity(emailUser)
                        finish()
                    } else {
                        showErrorSnackBar("Wprowadzono nieprawidłowy email lub hasło",true)
                    }
                }
        }
    }

    /**
     * Function to allow the user to go to the main activity of the application after entering
     * correct login details.
     *
     * @param email The user's email address.
     */
    private fun goToMainActivity(email: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("userID", email)
        startActivity(intent)
        finish()
    }
}