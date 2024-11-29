package com.example.healthyeyes.app.cloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * The UserDatabase class represents the data of the user using the application.
 *
 * @property name name of the user.
 * @property surname user name.
 * @property sex gender of the user.
 * @property yearOfBirth year of the user.
 * @property glasses defect response.
 * @property defectiveSight user's visual defect.
 * @property login user login.
 * @property password user password.
 * @constructor Creates an object of class UserDatabase with the given values or initialises them.
 */
data class UserDatabase(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("surname") @set:PropertyName("surname") var surname: String = "",
    @get:PropertyName("sex") @set:PropertyName("sex") var sex: String = "",
    @get:PropertyName("yearOfBirth") @set:PropertyName("yearOfBirth") var yearOfBirth: String = "",
    @get:PropertyName("glasses") @set:PropertyName("glasses") var glasses: Boolean = false,
    @get:PropertyName("defectiveSight") @set:PropertyName("defectiveSight") var defectiveSight: String = "",
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String = "",
)