package com.example.healthyeyes.app.registration
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.healthyeyes.R
import com.example.healthyeyes.MainActivityLogin
import com.example.healthyeyes.app.cloudFirestore.FirestoreDatabaseFunction
import com.example.healthyeyes.app.cloudFirestore.UserDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * Activity that allows the user to complete the registration and create an account
 * for the application.
 */
class MainActivityRegistration2 : BaseActivity() {
    private val database = Firebase.firestore
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var repeatedPassword: EditText
    private lateinit var creatingAccountButton: Button
    private lateinit var dbFunction: FirestoreDatabaseFunction

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_page2)

        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val sexString = intent.getStringExtra("sex")
        val sex = when (sexString) {
            "MALE" -> Gender.MALE
            else -> Gender.FEMALE
        }
        val yearOfBirth = intent.getStringExtra("yearOfBirth")
        val glassesString = intent.getStringExtra("glasses")
        val glasses = glassesString == "Tak"
        val defectiveSight = intent.getStringExtra("defectiveSight")

        dbFunction = FirestoreDatabaseFunction(database)
        email = findViewById(R.id.emailInputRegistration)
        password = findViewById(R.id.inputPasswordRegistration)
        repeatedPassword = findViewById(R.id.inputPasswordRegistration2)
        creatingAccountButton = findViewById(R.id.creatingAccountButton)

        creatingAccountButton.setOnClickListener {
            registerUser(
                name.toString(),
                surname.toString(),
                sex,
                yearOfBirth.toString(),
                glasses,
                defectiveSight.toString()
            )
        }
    }

    /**
     * Method that validates the registration data entered by the user.
     *
     * @return True if the data entered is valid and False if it is not.
     */
    private fun validateRegisterDetails(): Boolean {
        val passwordText = password.text.toString().trim()
        return when {
            TextUtils.isEmpty(email.text.toString().trim()) -> {
                showErrorSnackBar("Proszę wprowadzić adres email", true)
                false
            }
            TextUtils.isEmpty(passwordText) -> {
                showErrorSnackBar("Proszę wprowadzić hasło", true)
                false
            }
            passwordText.length < 6 -> {
                showErrorSnackBar("Hasło musi mieć co najmniej 6 znaków", true)
                false
            }
            TextUtils.isEmpty(repeatedPassword.text.toString().trim()) -> {
                showErrorSnackBar("Proszę powtórzyć hasło", true)
                false
            }
            else -> true
        }
    }

    /**
     * User registration method.
     *
     * @param name First name of user.
     * @param surname User's surname.
     * @param sex Gender of user.
     * @param yearOfBirth Year of birth of user.
     * @param glasses Question if the user has a visual impairment.
     * @param defectiveSight Visual impairment.
     */
    private fun registerUser(
        name: String,
        surname: String,
        sex: Gender,
        yearOfBirth: String,
        glasses: Boolean,
        defectiveSight: String
    ) {
        if (validateRegisterDetails()) {
            val login: String = email.text.toString().trim()
            val password: String = password.text.toString().trim()
            val repeatedPassword: String = repeatedPassword.text.toString().trim()
            val hashedPassword = hashPassword(password)
            val hashedRepeatedPassword = hashPassword(repeatedPassword)
            if (hashedPassword != hashedRepeatedPassword) {
                showErrorSnackBar("Hasła nie są takie same. Spróbuj ponownie.", true)
                return
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackBar(
                            "Zarejestrowano pomyślnie. Twoje id to ${firebaseUser.uid}",
                            false
                        )
                        FirebaseAuth.getInstance().signOut()
                        finish()
                        val intent = Intent(this, MainActivityLogin::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            val user = UserDatabase(
                name,
                surname,
                sex.toString(),
                yearOfBirth,
                glasses,
                defectiveSight,
                login,
                hashedPassword
            )
            GlobalScope.launch(Dispatchers.Main) {
                dbFunction.addUser(login, user)
            }
        }
    }

    /**
     * User password hashing method.
     *
     * @param password Hashable password.
     * @return hashed password.
     */
    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}