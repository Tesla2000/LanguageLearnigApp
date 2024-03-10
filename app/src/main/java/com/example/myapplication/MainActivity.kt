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
		arrayListOf("the ability", "die Fähigkeit"),
		arrayListOf("able", "fähig"),
		arrayListOf("about", "um"),
		arrayListOf("the above", "obenstehendes"),
		arrayListOf("accept", "akzeptieren"),
		arrayListOf("according", "nach"),
		arrayListOf("the account", "das Konto"),
		arrayListOf("across", "über"),
		arrayListOf("the act", "Der Akt"),
		arrayListOf("the action", "die Aktion"),
		arrayListOf("the activity", "die Aktivität"),
		arrayListOf("actually", "Genau genommen"),
		arrayListOf("the add", "das Hinzufügen"),
		arrayListOf("the address", "die Adresse"),
		arrayListOf("the administration", "die Verwaltung"),
		arrayListOf("admit", "zugeben"),
		arrayListOf("the adult", "der Erwachsene"),
		arrayListOf("the affect", "der Affekt"),
		arrayListOf("after", "nach"),
		arrayListOf("again", "wieder"),
		arrayListOf("against", "gegen"),
		arrayListOf("the age", "das Alter"),
		arrayListOf("the agency", "die Agentur"),
		arrayListOf("the agent", "Der Agent"),
		arrayListOf("ago", "vor"),
		arrayListOf("agree", "zustimmen"),
		arrayListOf("the agreement", "die Vereinbarung"),
		arrayListOf("ahead", "voraus"),
		arrayListOf("the air", "die Luft"),
		arrayListOf("all", "alle"),
		arrayListOf("allow", "erlauben"),
		arrayListOf("almost", "fast"),
		arrayListOf("alone", "allein"),
		arrayListOf("along", "entlang"),
		arrayListOf("already", "bereits"),
		arrayListOf("also", "Auch"),
		arrayListOf("although", "Obwohl"),
		arrayListOf("always", "stets"),
		arrayListOf("the American", "die Amerikaner"),
		arrayListOf("among", "unter"),
		arrayListOf("the amount", "die Summe"),
		arrayListOf("the analysis", "Die Analyse"),
		arrayListOf("and", "Und"),
		arrayListOf("the animal", "das Tier"),
		arrayListOf("another", "ein anderer"),
		arrayListOf("the answer", "die Antwort"),
		arrayListOf("any", "beliebig"),
		arrayListOf("anyone", "irgendjemand"),
		arrayListOf("anything", "irgendetwas"),
		arrayListOf("appear", "erscheinen"),
		arrayListOf("apply", "anwenden"),
		arrayListOf("the approach", "die Vorgehensweise"),
		arrayListOf("the area", "das Gebiet"),
		arrayListOf("argue", "argumentieren"),
		arrayListOf("the arm", "der Arm"),
		arrayListOf("around", "um"),
		arrayListOf("arrive", "ankommen"),
		arrayListOf("the art", "die Kunst"),
		arrayListOf("the article", "der Artikel"),
		arrayListOf("the artist", "der Künstler"),
		arrayListOf("the as", "das as"),
		arrayListOf("ask", "fragen"),
		arrayListOf("assume", "annehmen"),
		arrayListOf("the at", "das at"),
		arrayListOf("the attack", "der Angriff"),
		arrayListOf("the attention", "die Aufmerksamkeit"),
		arrayListOf("the attorney", "der Anwalt"),
		arrayListOf("the audience", "die Zuschauer"),
		arrayListOf("the author", "der Autor"),
		arrayListOf("the authority", "die Autorität"),
		arrayListOf("available", "verfügbar"),
		arrayListOf("avoid", "vermeiden"),
		arrayListOf("away", "weg"),
		arrayListOf("the baby", "das Baby"),
		arrayListOf("the back", "der Rücken"),
		arrayListOf("the bad", "das Schlechte"),
		arrayListOf("the bag", "die Tasche"),
		arrayListOf("the ball", "der Ball"),
		arrayListOf("the bank", "die Bank"),
		arrayListOf("the bar", "die Bar"),
		arrayListOf("the base", "die Basis"),
		arrayListOf("the be", "das Sein"),
		arrayListOf("the beat", "der Beat"),
		arrayListOf("beautiful", "Schön"),
		arrayListOf("because", "Weil"),
		arrayListOf("become", "werden"),
		arrayListOf("the bed", "das Bett"),
		arrayListOf("before", "Vor"),
		arrayListOf("the begin", "der Anfang"),
		arrayListOf("the behavior", "das Verhalten"),
		arrayListOf("the behind", "der Hintern"),
		arrayListOf("believe", "glauben"),
		arrayListOf("the benefit", "der Vorteil"),
		arrayListOf("the best", "der beste"),
		arrayListOf("the better", "desto besser"),
		arrayListOf("between", "zwischen"),
		arrayListOf("beyond", "darüber hinaus"),
		arrayListOf("big", "groß"),
		arrayListOf("the bill", "die Rechnung"),
		arrayListOf("the billion", "die Milliarden"),
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
