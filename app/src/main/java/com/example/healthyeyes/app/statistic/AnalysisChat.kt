package com.example.healthyeyes.app.statistic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyeyes.MainViewApp
import com.example.healthyeyes.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * The `AnalysisChat` activity allows users to send questions to an AI model (OpenAI's GPT-3.5) and
 * receive responses.
 */
class AnalysisChat : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var answer: TextView
    private lateinit var sentQuestion: TextView
    private lateinit var questionToAnalysis: TextInputEditText
    private lateinit var backIntoRaports: Button
    private lateinit var sendQuestionButton: Button
    private var question1: String? = null

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.analysis_chat)
        questionToAnalysis = findViewById(R.id.questionToAnalysis)
        sentQuestion = findViewById(R.id.sentQuestion)
        answer = findViewById(R.id.answer)
        backIntoRaports = findViewById(R.id.backIntoRaports)
        sendQuestionButton = findViewById(R.id.sendQuestionButton)
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        question1 = intent.getStringExtra("question")
        questionToAnalysis.setText(
            question1
        )

        sendQuestionButton.setOnClickListener {
            answer.text = "Proszę czekać..."
            val question = questionToAnalysis.text.toString().trim()
            if (question.isNotEmpty()) {
                getResponse(question) { response ->
                    runOnUiThread {
                        answer.text = response
                    }
                }
            }
        }
        questionToAnalysis.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                answer.text = "Proszę czekać..."
                val question = questionToAnalysis.text.toString().trim()
                if (question.isNotEmpty()) {
                    getResponse(question) { response ->
                        runOnUiThread {
                            answer.text = response
                        }
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })
        backIntoRaports.setOnClickListener {
            openRaportsView(userId.toString())
        }
    }

    /**
     * Navigates back to the reports view.
     *
     * @param userID The user's ID (email) to pass along to the next screen.
     */
    private fun openRaportsView(userID: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }

    /**
     * Sends a question to the OpenAI API and handles the response.
     *
     * @param question The question submitted by the user.
     * @param callback A function to handle the response from the API.
     */
    private fun getResponse(question: String, callback: (String) -> Unit) {
        sentQuestion.text = question
        questionToAnalysis.setText(question)

        val apiKey = "API_KEY"
        val url = "https://api.openai.com/v1/chat/completions"
        val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {"role": "user", "content": "$question"}
                ],
                "max_tokens": 500,
                "temperature": 0
            }
        """.trimIndent()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "Żądanie sieciowe nie powiodło się", e)
                runOnUiThread {
                    answer.text = "Błąd sieci: ${e.message}"
                }
            }

            /**
             * Handles the response from the OpenAI API.
             *
             * @param call The HTTP call that was made.
             * @param response The response from the server.
             */
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("error", "Nieoczekiwany kod odpowiedzi: ${response.code}")
                    val errorBody = response.body?.string()
                    Log.e("error", "Treść odpowiedzi: $errorBody")
                    runOnUiThread {
                        answer.text = "Nieoczekiwana odpowiedź: ${response.message}\nKod: ${response.code}\nTreść: $errorBody"
                    }
                    return
                }
                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    Log.e("error", "Odpowiedź jest pusta")
                    runOnUiThread {
                        answer.text = "Otrzymano pustą odpowiedź z serwera"
                    }
                    return
                }
                try {
                    val jsonObject = JSONObject(body)
                    val choicesArray = jsonObject.getJSONArray("choices")
                    val message = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")
                    runOnUiThread {
                        callback(message)
                    }
                } catch (e: Exception) {
                    Log.e("error", "Błąd parsowania JSON", e)
                    runOnUiThread {
                        answer.text = "Błąd parsowania odpowiedzi: ${e.message}"
                    }
                }
            }
        })
    }
}
