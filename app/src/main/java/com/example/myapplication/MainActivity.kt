package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Locale
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val RQ_SPEECH_REC = 102
    private lateinit var tts: TextToSpeech
    private var userAnswer: String = ""
    private var askingSpeechInput = false
    private val repetitionRate = 0.5f
	private val speechRate = 0.5f
    private val words = Words()
    private val login = "Login"
    private val url = "https://tesla2000.pythonanywhere.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testQuestionAnswer()
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
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.GERMANY.toString())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            userAnswer = result?.get(0).toString()
            askingSpeechInput = false
        }
    }

    private fun testQuestionAnswer() {
        val question = words.unansweredQuestionAnswerPairsBuffer[0][0]
        val answer = words.unansweredQuestionAnswerPairsBuffer[0][1]
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(speechRate)
                val params = HashMap<String, String>()
                params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
                tts.speak(question, TextToSpeech.QUEUE_ADD, params)
            }
        }

        tts.setOnUtteranceCompletedListener { utteranceId ->
            do {
                if (!askingSpeechInput) {
                    askingSpeechInput = true
                    askSpeechInput()
                }
            } while (userAnswer == "")
            Log.i("USER", userAnswer)
            Log.i("USER", answer)
            postAnswer(question, answer, userAnswer)
        }
    }

    private fun afterPost(answer: String) {
        if (userAnswer.lowercase().replace(Regex("[.,?!]"), "") == answer.lowercase().replace(Regex("[.,?!]"), "")) {
            Log.i("USER", "Answer correct")
            val removedElement = words.unansweredQuestionAnswerPairsBuffer.removeFirst()
            if (!words.answeredQuestionAnswerPairs.contains(removedElement)) {
                Log.i("USER", "Adding $removedElement to answered")
                words.answeredQuestionAnswerPairs.add(removedElement)
            } else {
                Log.i("USER", "$removedElement already in answered $words.answeredQuestionAnswerPairs")
            }
            if (words.unansweredQuestionAnswerPairs.isEmpty() || Random.nextFloat() < repetitionRate) {
                val randomElement = words.answeredQuestionAnswerPairs.random()
                Log.i("USER", "Adding $randomElement from answeredQuestionAnswerPairs to buffer")
                words.unansweredQuestionAnswerPairsBuffer.add(randomElement)
            } else {
                val randomElement = words.unansweredQuestionAnswerPairs.random()
                Log.i("USER", "Adding $randomElement from unansweredQuestionAnswerPairs to buffer")
                words.unansweredQuestionAnswerPairsBuffer.add(randomElement)
                words.unansweredQuestionAnswerPairs.remove(randomElement)
            }
            words.unansweredQuestionAnswerPairsBuffer.add(removedElement)
            userAnswer = ""
            askingSpeechInput = false
            confirmAnswer()
        } else {
            sayCorrectAnswer()
            words.unansweredQuestionAnswerPairsBuffer.add(words.unansweredQuestionAnswerPairsBuffer.removeFirst())
            userAnswer = ""
            askingSpeechInput = false
        }
    }

    private fun postAnswer(question: String, answer: String, userAnswer: String) {
        Log.i("API", "POSTING\nquestion $question\nanswer $userAnswer")

        val json = "$login;$question;$userAnswer".trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("API", "Failed to POST ${e.message}")
                afterPost(answer)

            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("API", "Succeeded to POST ${response.body?.string()}")
                afterPost(answer)
            }
        }
        )
    }

	private fun sayCorrectAnswer() {
		val answer = words.unansweredQuestionAnswerPairsBuffer[0][1]
		tts = TextToSpeech(applicationContext) {
			if (it == TextToSpeech.SUCCESS) {
				tts.language = Locale.GERMAN
				tts.setSpeechRate(speechRate)
				val params = HashMap<String, String>()
				params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
				tts.speak(answer, TextToSpeech.QUEUE_ADD, params)
			}
		}
		tts.setOnUtteranceCompletedListener { utteranceId ->
			testQuestionAnswer()
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
