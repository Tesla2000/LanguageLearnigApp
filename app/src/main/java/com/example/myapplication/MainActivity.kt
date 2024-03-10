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
		arrayListOf("Tell me about your day.", "Erzähl mir von deinem Tag."),
		arrayListOf("All dogs bark.", "Alle Hunde bellen."),
		arrayListOf("She is also coming.", "Sie kommt auch."),
		arrayListOf("Tom and Jerry are friends.", "Tom und Jerry sind Freunde."),
		arrayListOf("As the sun sets, it gets dark.", "Wenn die Sonne untergeht, wird es dunkel."),
		arrayListOf("Meet me at noon.", "Treffen wir uns um zwölf Uhr mittags."),
		arrayListOf("To be or not to be?", "Sein oder nicht sein?"),
		arrayListOf("She's sad because it's raining.", "Sie ist traurig, weil es regnet."),
		arrayListOf("I want to go, but I can't.", "Ich möchte gehen, aber ich kann nicht."),
		arrayListOf("The book was written by her.", "Das Buch wurde von ihr geschrieben."),
		arrayListOf("You can do it!", "Du kannst es schaffen!"),
		arrayListOf("Come here.", "Komm hierher."),
		arrayListOf("Could you please help me?", "Könntest du mir bitte helfen?"),
		arrayListOf("Today is a good day.", "Heute ist ein guter Tag."),
		arrayListOf("What should I do?", "Was soll ich tun?"),
		arrayListOf("I'm tired, even though I slept.", "Ich bin müde, obwohl ich geschlafen habe."),
		arrayListOf("I can't find my keys.", "Ich kann meine Schlüssel nicht finden."),
		arrayListOf("She arrived first.", "Sie kam zuerst an."),
		arrayListOf("This is for you.", "Das ist für dich."),
		arrayListOf("I'm from Italy.", "Ich komme aus Italien."),
		arrayListOf("I want to get ice cream.", "Ich möchte ein Eis bekommen."),
		arrayListOf("Give me that.", "Gib mir das."),
		arrayListOf("Let's go home.", "Lass uns nach Hause gehen."),
		arrayListOf("I have a pen.", "Ich habe einen Stift."),
		arrayListOf("He is my brother.", "Er ist mein Bruder."),
		arrayListOf("I saw her yesterday.", "Ich habe sie gestern gesehen."),
		arrayListOf("Come here.", "Komm her."),
		arrayListOf("I like him.", "Ich mag ihn."),
		arrayListOf("That's his car.", "Das ist sein Auto."),
		arrayListOf("How are you?", "Wie geht es dir?"),
		arrayListOf("I am hungry.", "Ich habe Hunger."),
		arrayListOf("If it rains, bring an umbrella.", "Wenn es regnet, bring einen Regenschirm mit."),
		arrayListOf("I'm in a hurry.", "Ich habe es eilig."),
		arrayListOf("He went into the house.", "Er ging ins Haus."),
		arrayListOf("It's cold.", "Es ist kalt."),
		arrayListOf("The tree lost its leaves.", "Der Baum hat seine Blätter verloren."),
		arrayListOf("Just do it.", "Tu es einfach."),
		arrayListOf("I know him.", "Ich kenne ihn."),
		arrayListOf("I like chocolate.", "Ich mag Schokolade."),
		arrayListOf("Look over there.", "Schau dort hinüber."),
		arrayListOf("Can you make a cake?", "Kannst du einen Kuchen backen?"),
		arrayListOf("He's a good man.", "Er ist ein guter Mann."),
		arrayListOf("There are many books.", "Es gibt viele Bücher."),
		arrayListOf("He told me.", "Er hat es mir erzählt."),
		arrayListOf("I want more.", "Ich möchte mehr."),
		arrayListOf("This is my book.", "Das ist mein Buch."),
		arrayListOf("I got a new phone.", "Ich habe ein neues Handy bekommen."),
		arrayListOf("No way!", "Auf keinen Fall!"),
		arrayListOf("I'm not going.", "Ich gehe nicht."),
		arrayListOf("It's now or never.", "Jetzt oder nie."),
		arrayListOf("A piece of cake.", "Ein Kinderspiel."),
		arrayListOf("It's on the table.", "Es liegt auf dem Tisch."),
		arrayListOf("One apple, please.", "Ein Apfel, bitte."),
		arrayListOf("I have only one.", "Ich habe nur einen."),
		arrayListOf("Tea or coffee?", "Tee oder Kaffee?"),
		arrayListOf("The other day.", "Neulich."),
		arrayListOf("This is our house.", "Das ist unser Haus."),
		arrayListOf("Let's go out.", "Lass uns ausgehen."),
		arrayListOf("People are nice.", "Die Leute sind nett."),
		arrayListOf("What did you say?", "Was hast du gesagt?"),
		arrayListOf("I see you.", "Ich sehe dich."),
		arrayListOf("She is here.", "Sie ist hier."),
		arrayListOf("It's raining, so bring an umbrella.", "Es regnet, also bring einen Regenschirm mit."),
		arrayListOf("I want some candy.", "Ich möchte etwas Süßes."),
		arrayListOf("Take this.", "Nimm das."),
		arrayListOf("Tell me a story.", "Erzähl mir eine Geschichte."),
		arrayListOf("He's taller than me.", "Er ist größer als ich."),
		arrayListOf("That's mine.", "Das gehört mir."),
		arrayListOf("The dog is sleeping.", "Der Hund schläft."),
		arrayListOf("It's their turn.", "Es ist ihre Reihe."),
		arrayListOf("I saw them.", "Ich habe sie gesehen."),
		arrayListOf("Then what happened?", "Was passierte dann?"),
		arrayListOf("There it is!", "Da ist es!"),
		arrayListOf("These are mine.", "Diese gehören mir."),
		arrayListOf("They are coming.", "Sie kommen."),
		arrayListOf("What's that thing?", "Was ist das für ein Ding?"),
		arrayListOf("I think so.", "Ich denke schon."),
		arrayListOf("This is nice.", "Das ist schön."),
		arrayListOf("I like those.", "Ich mag die."),
		arrayListOf("Time flies.", "Die Zeit vergeht schnell."),
		arrayListOf("I want to go.", "Ich möchte gehen."),
		arrayListOf("I have two apples.", "Ich habe zwei Äpfel."),
		arrayListOf("Look up.", "Schau nach oben."),
		arrayListOf("Use this.", "Benutze das."),
		arrayListOf("It's very hot.", "Es ist sehr heiß."),
		arrayListOf("I want ice cream.", "Ich möchte ein Eis."),
		arrayListOf("What's the best way?", "Was ist der beste Weg?"),
		arrayListOf("We are friends.", "Wir sind Freunde."),
		arrayListOf("I hope you're well.", "Ich hoffe, es geht dir gut."),
		arrayListOf("What is that?", "Was ist das?"),
		arrayListOf("When will you go?", "Wann wirst du gehen?"),
		arrayListOf("Which one?", "Welcher?"),
		arrayListOf("Who are you?", "Wer bist du?"),
		arrayListOf("I will do it.", "Ich werde es tun."),
		arrayListOf("Come with me.", "Komm mit mir."),
		arrayListOf("Would you like some tea?", "Möchtest du etwas Tee?"),
		arrayListOf("It's a new year.", "Es ist ein neues Jahr."),
		arrayListOf("It's you.", "Es bist du."),
		arrayListOf("Is this your book?", "Ist das dein Buch?"),
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
            if (userAnswer.lowercase().replace(Regex("[.,?!]"), "") == answer.lowercase().replace(Regex("[.,?!]"), "")) {
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
