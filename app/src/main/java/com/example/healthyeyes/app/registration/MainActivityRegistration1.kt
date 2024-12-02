package com.example.healthyeyes.app.registration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import com.example.healthyeyes.R

/**
 * Activity that allows the user to register for the application.
 */
class MainActivityRegistration1 : BaseActivity() {
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var yearOfBirth: EditText
    private lateinit var sex: Spinner
    private lateinit var glasses: Switch
    private lateinit var registrationButton: Button
    private lateinit var setYesNo: TextView
    private lateinit var defectiveSight: EditText

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_page1)

        name = findViewById(R.id.nameInput)
        surname = findViewById(R.id.surnameInput)
        yearOfBirth = findViewById(R.id.yearOfBirthInput)
        sex = findViewById(R.id.sexSpinner)
        glasses = findViewById(R.id.glassesQuestionsSwitch)
        setYesNo = findViewById(R.id.setYesNoText)
        registrationButton = findViewById(R.id.signInButtonRegistration)
        defectiveSight = findViewById(R.id.defectiveSight)
        val genderAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.gender_array)
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.BLACK)
                return view
            }
        }
        sex.adapter = genderAdapter
        glasses.setOnCheckedChangeListener { _, isChecked ->
            setYesNo.text = if (isChecked) "Tak" else "Nie"
            defectiveSight.isEnabled = isChecked
        }
        registrationButton.setOnClickListener {
            registerUser()
        }
    }

    /**
     * Method that validates the registration data entered by the user.
     *
     * @return True when the entered data is valid, False when it is not.
     */
    private fun validateRegisterDetails(): Boolean {
        val nameUser = name.text.toString().trim()
        val surnameUser = surname.text.toString().trim()
        val sexUser = sex.selectedItem.toString()
        val yearOfBirthUser = yearOfBirth.text.toString().trim()
        val glassesUser = glasses.isChecked

        if (TextUtils.isEmpty(nameUser)) {
            showErrorSnackBar("Proszę wprowadzić swoje imię", true)
            return false
        }
        if (TextUtils.isEmpty(surnameUser)) {
            showErrorSnackBar("Proszę wprowadzić swoje nazwisko", true)
            return false
        }
        if (TextUtils.isEmpty(sexUser)) {
            showErrorSnackBar("Proszę wprowadzić płeć", true)
            return false
        }
        if (TextUtils.isEmpty(yearOfBirthUser)) {
            showErrorSnackBar("Proszę wprowadzić rok swojego urodzenia", true)
            return false
        }
        if (glassesUser) {
            val defectiveSightUser = defectiveSight.text.toString().trim()
            if (TextUtils.isEmpty(defectiveSightUser)) {
                showErrorSnackBar("Proszę wprowadzić wadę wzroku", true)
                return false
            }
        }
        return true
    }

    /**
     * Method that registers the user after verification of the registration data.
     */
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val name = name.text.toString().trim()
            val surname = surname.text.toString().trim()
            val sexString = sex.selectedItem.toString()
            val yearOfBirth = yearOfBirth.text.toString().trim()
            val glasses = glasses.isChecked
            val defectiveSight = defectiveSight.text.toString().trim()
            val sex = when (sexString) {
                "Mężczyzna" -> Gender.MALE
                else -> Gender.FEMALE
            }
            openActivity(
                name,
                surname,
                sex,
                yearOfBirth,
                glasses,
                defectiveSight
            )
        }
    }

    /**
     * Method of opening another registration and data transfer activity.
     *
     * @param name Name of the user.
     * @param surname Surname of the user.
     * @param sex Gender of the user.
     * @param yearOfBirth Year of birth of the user.
     * @param glasses Question if the user has a visual impairment.
     * @param defectiveSight Defective sight.
     */
    private fun openActivity(
        name: String,
        surname: String,
        sex: Gender,
        yearOfBirth: String,
        glasses: Boolean,
        defectiveSight: String
    ) {
        val intent = Intent(this, MainActivityRegistration2::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("sex", sex.toString())
        intent.putExtra("yearOfBirth", yearOfBirth)
        intent.putExtra("glasses", if (glasses) "Tak" else "Nie")
        intent.putExtra("defectiveSight", defectiveSight)
        startActivity(intent)
    }
}