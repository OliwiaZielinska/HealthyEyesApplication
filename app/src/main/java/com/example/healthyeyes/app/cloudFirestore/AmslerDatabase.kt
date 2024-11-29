package com.example.healthyeyes.app.cloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * The AmslerDatabase class represents the user results obtained after the Amsler test.
 *
 * @property userId unique user identifier.
 * @property date date the test was performed.
 * @property hour hour of test execution.
 * @property leftEye test result obtained for the left eye.
 * @property rightEye test result obtained for the right eye.
 * @constructor Creates an object of class AmslerDatabase with the given values or initialises them.
 */
data class AmslerDatabase(
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("hour") @set:PropertyName("hour") var hour: String = "00:00",
    @get:PropertyName("leftEye") @set:PropertyName("leftEye") var leftEye: String = "",
    @get:PropertyName("rightEye") @set:PropertyName("rightEye") var rightEye: String = "",
)