package com.example.healthyeyes.app.cloudFirestore

import com.google.firebase.firestore.PropertyName

/**
 * The SnellenDatabase class represents the user's results obtained after performing the Snellen test.
 *
 * @property userId unique user identifier.
 * @property date date the test was performed.
 * @property hour hour of test execution.
 * @property leftEye test result obtained for left eye.
 * @property rightEye test result obtained for the right eye.
 * @constructor Creates an object of class SnellenDatabase with the given values or initialises them.
 */
data class SnellenDatabase(
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("hour") @set:PropertyName("hour") var hour: String = "00:00",
    @get:PropertyName("leftEye") @set:PropertyName("leftEye") var leftEye: String = "",
    @get:PropertyName("rightEye") @set:PropertyName("rightEye") var rightEye: String = "",
)