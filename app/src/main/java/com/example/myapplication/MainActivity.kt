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
    private val bufferSize = 2
	private val speechRate = 0.5f
    private val unansweredQuestionAnswerPairs: ArrayList<ArrayList<String>> = arrayListOf(
		arrayListOf("about", "um"),
		arrayListOf("all", "alle"),
		arrayListOf("also", "Auch"),
		arrayListOf("and", "Und"),
		arrayListOf("as", "als"),
		arrayListOf("the at", "das at"),
		arrayListOf("be", "Sei"),
		arrayListOf("because", "Weil"),
		arrayListOf("but", "Aber"),
		arrayListOf("by", "von"),
		arrayListOf("can", "dürfen"),
		arrayListOf("come", "kommen"),
		arrayListOf("could", "könnte"),
		arrayListOf("the day", "der Tag"),
		arrayListOf("do", "Tun"),
		arrayListOf("even", "sogar"),
		arrayListOf("find", "finden"),
		arrayListOf("first", "Erste"),
		arrayListOf("for", "für"),
		arrayListOf("from", "aus"),
		arrayListOf("get", "erhalten"),
		arrayListOf("give", "geben"),
		arrayListOf("go", "gehen"),
		arrayListOf("have", "haben"),
		arrayListOf("he", "er"),
		arrayListOf("here", "Hier"),
		arrayListOf("him", "ihn"),
		arrayListOf("his", "sein"),
		arrayListOf("how", "Wie"),
		arrayListOf("I", "Ich"),
		arrayListOf("if", "Wenn"),
		arrayListOf("in", "In"),
		arrayListOf("into", "hinein"),
		arrayListOf("tit", "es"),
		arrayListOf("its", "seine"),
		arrayListOf("just", "Nur"),
		arrayListOf("know", "wissen"),
		arrayListOf("like", "wie"),
		arrayListOf("look", "sehen"),
		arrayListOf("make", "machen"),
		arrayListOf("man", "Mann"),
		arrayListOf("many", "viele"),
		arrayListOf("me", "ich"),
		arrayListOf("more", "mehr"),
		arrayListOf("my", "Mein"),
		arrayListOf("new", "neu"),
		arrayListOf("no", "NEIN"),
		arrayListOf("not", "nicht"),
		arrayListOf("now", "Jetzt"),
		arrayListOf("of", "von"),
		arrayListOf("on", "An"),
		arrayListOf("one", "eins"),
		arrayListOf("only", "nur"),
		arrayListOf("the or", "das oder"),
		arrayListOf("other", "andere"),
		arrayListOf("our", "unser"),
		arrayListOf("out", "aus"),
		arrayListOf("people", "Menschen"),
		arrayListOf("say", "sagen"),
		arrayListOf("see", "sehen"),
		arrayListOf("she", "sie"),
		arrayListOf("so", "Also"),
		arrayListOf("some", "manche"),
		arrayListOf("take", "nehmen"),
		arrayListOf("tell", "erzählen"),
		arrayListOf("than", "als"),
		arrayListOf("that", "Das"),
		arrayListOf("the", "Die"),
		arrayListOf("their", "ihre"),
		arrayListOf("them", "ihnen"),
		arrayListOf("then", "Dann"),
		arrayListOf("there", "Dort"),
		arrayListOf("these", "diese"),
		arrayListOf("they", "Sie"),
		arrayListOf("the thing", "die Sache"),
		arrayListOf("think", "denken"),
		arrayListOf("this", "Das"),
		arrayListOf("those", "diese"),
		arrayListOf("time", "Zeit"),
		arrayListOf("to", "Zu"),
		arrayListOf("two", "zwei"),
		arrayListOf("up", "hoch"),
		arrayListOf("use", "verwenden"),
		arrayListOf("very", "sehr"),
		arrayListOf("want", "wollen"),
		arrayListOf("way", "Weg"),
		arrayListOf("we", "Wir"),
		arrayListOf("well", "Also"),
		arrayListOf("what", "Was"),
		arrayListOf("when", "Wann"),
		arrayListOf("which", "welche"),
		arrayListOf("who", "WHO"),
		arrayListOf("will", "Wille"),
		arrayListOf("with", "mit"),
		arrayListOf("would", "würde"),
		arrayListOf("the year", "das Jahr"),
		arrayListOf("you", "Du"),
		arrayListOf("your", "dein"),
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
				unansweredQuestionAnswerPairsBuffer.add(removedElement)
				userAnswer = ""
				askingSpeechInput = false
				testQuestionAnswer()
            } else {
				sayCorrectAnswer()
				unansweredQuestionAnswerPairsBuffer.add(unansweredQuestionAnswerPairsBuffer.removeFirst())
				userAnswer = ""
				askingSpeechInput = false
			}
        }
    }

	private fun sayCorrectAnswer() {
		val answer = unansweredQuestionAnswerPairsBuffer[0][1]
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
}
