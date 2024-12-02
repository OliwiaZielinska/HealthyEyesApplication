package com.example.healthyeyes

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.example.healthyeyes.app.cloudFirestore.FirestoreDatabaseFunction
import com.example.healthyeyes.app.cloudFirestore.UserDatabase
import com.example.healthyeyes.app.registration.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * A class that allows the user to make changes to the database related to visual impairment and
 * to change the password or possibly delete the account if the user does not wish to use
 * the application.
 */
class Settings : BaseActivity() {
    private lateinit var settingsYesNoSwitch: Switch
    private lateinit var settingsYesNoText: TextView
    private lateinit var editDefectiveSight: EditText
    private lateinit var inputOldPassword: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var inputConfirmNewPassword: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var deleteAccountButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val dbOperations = FirestoreDatabaseFunction(db)
    private var userID: String = ""

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        userID = intent.getStringExtra("userID").toString()

        settingsYesNoSwitch = findViewById(R.id.settingsYesNoSwitch)
        settingsYesNoText = findViewById(R.id.settingsYesNoText)
        editDefectiveSight = findViewById(R.id.editDefectiveSightInput)
        inputOldPassword = findViewById(R.id.inputOldPasswordRegistration)
        inputNewPassword = findViewById(R.id.inputPasswordRegistration)
        inputConfirmNewPassword = findViewById(R.id.inputPasswordRegistration2)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)

        settingsYesNoSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsYesNoText.text = if (isChecked) "Tak" else "Nie"
            editDefectiveSight.isEnabled = isChecked
        }

        setData()

        saveChangesButton.setOnClickListener {
            saveChanges()
        }

        deleteAccountButton.setOnClickListener {
            deleteAccount()
        }
    }

    /**
     * A function that allows the user to change his or her password and information about
     * the visual impairment, if any.
     */
    private fun saveChanges() {
        val oldPassword = hashPassword(inputOldPassword.text.toString())
        val newPassword = inputNewPassword.text.toString()
        val confirmNewPassword = inputConfirmNewPassword.text.toString()
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Nowe hasło nie może być puste", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword != confirmNewPassword) {
            Toast.makeText(this, "Nowe hasła nie są zgodne", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        db.collection("users").document(userId.toString()).get()
            .addOnSuccessListener { document ->
                document.toObject(UserDatabase::class.java)?.let { currentUser ->
                    if (currentUser.password == oldPassword) {
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.updatePassword(newPassword)
                            ?.addOnSuccessListener {
                                val hashedNewPassword = hashPassword(newPassword)
                                currentUser.password = hashedNewPassword
                                db.collection("users").document(userId.toString()).set(currentUser)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show()
                                        navigateToMainViewApp()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Błąd: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            ?.addOnFailureListener { exception ->
                                Toast.makeText(this, "Błąd zmiany hasła: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Niepoprawne stare hasło", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd pobierania danych użytkownika: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Password hash function using SHA-256.
     *
     * @param password user password.
     */
    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * A function that takes the user to the MainViewApp class when the user has correctly made
     * changes to their eye defect or changed their password.
     */
    private fun navigateToMainViewApp() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * A function that allows a user to delete an account, both from the database and
     * Firebase Authentication.
     */
    private fun deleteAccount() {
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        GlobalScope.launch(Dispatchers.Main) {
            dbOperations.deleteUser(userId.toString())
        }
        FirebaseAuth.getInstance().currentUser?.delete()
        navigateToActivityMain()
    }

    /**
     * A function that takes the user to the ActivityMain class when he or she makes a mistake
     * while making changes to the sight defect or password.
     */
    private fun navigateToActivityMain() {
        val intent = Intent(this, ActivityMain::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
        finish()
    }

    /**
     * Function to retrieve user information from the database.
     */
    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        val ref = db.collection("users").document(userId.toString())
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val defectiveSight = document.getString("defectiveSight")
                val glasses = document.getBoolean("glasses") ?: false
                val setYesNo = document.getBoolean("glasses")?.let { if (it) "Tak" else "Nie" }
                editDefectiveSight.setText(defectiveSight)
                settingsYesNoSwitch.isChecked = glasses
                settingsYesNoText.text = setYesNo
            }
        }
    }
}
