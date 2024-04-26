package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Locale


class LearnActivity : LoggedClass() {
    private val RQ_SPEECH_REC = 102
    private lateinit var tts: TextToSpeech
    private var userAnswer: String = ""
    private var askingSpeechInput = false
    private val speechRate = 0.5f
    private val buffer: ArrayList<List<String>> = arrayListOf()
    private var reset: Boolean = true
    private var speaking: Boolean = false
    private var language: String? = null
    private val bufferSize: Int = 1
    private val maxRepetitions: Int = 2
    private var repetitions: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        reset = true
        language = intent.getStringExtra("language")
        val resetButton: Button = findViewById(R.id.resetBtn)
        resetButton.setOnClickListener {
            resetButton.isEnabled = false
            val intent = Intent(this, LearnActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("token", token)
            intent.putExtra("language", language)
            startActivity(intent)
            finish()
            resetButton.isEnabled = true
        }
        val changeLessonButton: Button = findViewById(R.id.changeLesson)
        changeLessonButton.setOnClickListener {
            changeLessonButton.isEnabled = false
            getLanguages(token, username)
            changeLessonButton.isEnabled = true
        }
        getInitialQuestions()
    }

    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available!", Toast.LENGTH_LONG).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getToLanguage().toString())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            userAnswer = result?.get(0).toString()
            askingSpeechInput = false
        }
    }

    private fun getToLanguage(): Locale {
        return Locale(language!!.split('_')[1], language!!.split('_')[1].uppercase())
    }

    private fun getFromLanguage(): Locale {
        return Locale(language!!.split('_')[0], language!!.split('_')[0].uppercase())
    }

    private fun testQuestionAnswer() {
        val question = buffer[0][0]
        val answer = buffer[0][1]
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = getFromLanguage()
                tts.setSpeechRate(speechRate)
                val params = HashMap<String, String>()
                params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
                tts.speak(question, TextToSpeech.QUEUE_ADD, params)
            }
        }

        tts.setOnUtteranceCompletedListener {
            do {
                if (!askingSpeechInput && !speaking) {
                    askingSpeechInput = true
                    askSpeechInput()
                }
            } while (userAnswer == "")
            speaking = true
            Log.i("USER", userAnswer)
            Log.i("USER", answer)
            postAnswer(question, answer, userAnswer)
            userAnswer = ""
        }
    }

    private fun afterPost(responseString: String) {
        if (responseString.isEmpty()) {
            userAnswer = ""
            val questionAnswerPair = buffer.removeFirst()
            buffer.add(questionAnswerPair)
            repetitions++
            sayCorrectAnswer(questionAnswerPair[1])
        } else {
            repetitions = 0
            buffer.removeFirst()
            buffer.add(responseString.split(';'))
            confirmAnswer()
        }
        speaking = false
    }

    private fun postAnswer(question: String, answer: String, userAnswer: String) {
        Log.i("API", "POSTING\nquestion $question\nanswer $userAnswer")

        val json = "$username;$question;$userAnswer".trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("$url$language")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("API", "Failed to POST ${e.message}")
                postAnswer(question, answer, userAnswer)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                Log.i("API", "Succeeded to POST $responseString")
                afterPost(responseString!!)
            }
        }
        )
    }

    private fun getInitialQuestions() {
        Log.i("API", "GETTING initial question")


        val request = Request.Builder()
            .url("$url$username/$language")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("API", "Failed to POST ${e.message}")
                getInitialQuestions()

            }

            override fun onResponse(call: Call, response: Response) {
                val stringResponse = response.body?.string()
                Log.i("API", "Succeeded to GET $stringResponse")
                buffer.add(stringResponse!!.split(';'))
                if (buffer.size < bufferSize) {
                    getInitialQuestions()
                } else {
                    testQuestionAnswer()
                }
            }
        }
        )
    }

    private fun sayCorrectAnswer(answer: String) {
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = getToLanguage()
                tts.setSpeechRate(speechRate)
                val params = HashMap<String, String>()
                params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
                tts.speak(answer, TextToSpeech.QUEUE_ADD, params)
            }
        }
        tts.setOnUtteranceCompletedListener { utteranceId ->
            if (repetitions > maxRepetitions) {
                repetitions = 0
                buffer.removeFirst()
                getInitialQuestions()
            }
            else testQuestionAnswer()
        }
    }

    private fun confirmAnswer() {
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(speechRate)
                val params = HashMap<String, String>()
                params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
                tts.speak("You are right!", TextToSpeech.QUEUE_ADD, params)
            }
        }
        tts.setOnUtteranceCompletedListener { utteranceId ->
            testQuestionAnswer()
        }
    }
}
