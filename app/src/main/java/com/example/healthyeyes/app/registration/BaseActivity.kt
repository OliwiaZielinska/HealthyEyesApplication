package com.example.healthyeyes.app.registration

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.healthyeyes.R
import com.google.android.material.snackbar.Snackbar

/**
 * A class that allows messages to be displayed - Snackbar.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Method to display Snackbar messages.
     * @param message Content of the message to display.
     * @param errorMessage Specifies whether the message is an error (true) or a success (false).
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        if (errorMessage) {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackbar.show()
    }
}