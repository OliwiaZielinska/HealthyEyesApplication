package com.example.healthyeyes.app.cloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * The IshiharaDatabase class represents the user's results obtained after performing the Ishihara test.
 *
 * @property userId unique identifier of the user.
 * @property date date the test was performed.
 * @property hour hour of test execution.
 * @property result the result of the test.
 * @constructor Creates an object of class IshiharaDatabase with the given values or initialises them.
 */
data class IshiharaDatabase(
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("hour") @set:PropertyName("hour") var hour: String = "00:00",
    @get:PropertyName("result") @set:PropertyName("result") var result: String = "",
)