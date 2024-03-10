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
import java.util.Locale
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val RQ_SPEECH_REC = 102
    private lateinit var tts: TextToSpeech
    private var userAnswer: String = ""
    private var askingSpeechInput = false
    private val repetitionRate = 0.5f
    private val bufferSize = 5
    private val unansweredQuestionAnswerPairs: ArrayList<ArrayList<String>> = arrayListOf(
        arrayListOf("mother", "die mutter"),
        arrayListOf("father", "der vater"),
        arrayListOf("cat", "die katze"),
        arrayListOf("dog", "der hund"),
        arrayListOf("brother", "der Bruder"),
        arrayListOf("sister", "die Schwester"),
    )
    private val unansweredQuestionAnswerPairsBuffer: ArrayList<ArrayList<String>> = ArrayList(unansweredQuestionAnswerPairs.shuffled().take(bufferSize))
    private val answeredQuestionAnswerPairs: ArrayList<ArrayList<String>> = arrayListOf()
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
        val question = unansweredQuestionAnswerPairsBuffer[0][0]
        val answer = unansweredQuestionAnswerPairsBuffer[0][1]
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(1.0f)
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
            if (userAnswer.lowercase() == answer.lowercase()) {
                Log.i("USER", "Answer correct")
                val removedElement = unansweredQuestionAnswerPairsBuffer.removeFirst()
                if (!answeredQuestionAnswerPairs.contains(removedElement)) {
                    Log.i("USER", "Adding $removedElement to answered")
                    answeredQuestionAnswerPairs.add(removedElement)
                } else {
                    Log.i("USER", "$removedElement already in answered $answeredQuestionAnswerPairs")
                }
                if (unansweredQuestionAnswerPairs.isEmpty() || Random.nextFloat() < repetitionRate) {
                    val randomElement = answeredQuestionAnswerPairs.random()
                    Log.i("USER", "Adding $randomElement from answeredQuestionAnswerPairs to buffer")
                    unansweredQuestionAnswerPairsBuffer.add(randomElement)
                } else {
                    val randomElement = unansweredQuestionAnswerPairs.random()
                    Log.i("USER", "Adding $randomElement from unansweredQuestionAnswerPairs to buffer")
                    unansweredQuestionAnswerPairsBuffer.add(randomElement)
                    unansweredQuestionAnswerPairs.remove(randomElement)
                }
            }
            unansweredQuestionAnswerPairsBuffer.add(unansweredQuestionAnswerPairsBuffer.removeFirst())
            userAnswer = ""
            askingSpeechInput = false
            testQuestionAnswer()
        }
    }
}
