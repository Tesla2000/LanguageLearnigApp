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

class MainActivity : AppCompatActivity() {
    private val RQ_SPEECH_REC = 102
    private lateinit var tts: TextToSpeech
    private lateinit var userAnswer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var questionAnswerPairs: HashMap<String, String> = hashMapOf("mother" to "die mutter")

        for ((question, answer) in questionAnswerPairs) {
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
                if (utteranceId == "utteranceId") {
                    askSpeechInput()
                }
            }
        }
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
            val result = data?.getStringArrayExtra(RecognizerIntent.EXTRA_RESULTS)
            userAnswer = result?.get(0).toString()
        }
    }
}
