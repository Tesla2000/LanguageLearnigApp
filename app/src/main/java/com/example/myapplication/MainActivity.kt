package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : LoggedClass() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createButtons(intent.getStringArrayListExtra("languages")!!)
    }

    private fun createButtons(buttonNames: List<String>){

        val relativeLayout = findViewById<RelativeLayout>(R.id.relativeLayout)
        val buttons: ArrayList<Button> = arrayListOf()
        for (i in buttonNames.indices) {
            val button = Button(this)
            button.text = buttonNames[i]
            button.textSize = 24f
            button.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            button.id = i + 1
            relativeLayout.addView(button)
            buttons.add(button)
        }
        for (button in buttons) {
            button.setOnClickListener {
                for (b in buttons)
                    b.isEnabled = false
                val intent = Intent(this, LearnActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("token", token)
                intent.putExtra("language", button.text)
                startActivity(intent)
                finish()
            }
        }
    }
}
