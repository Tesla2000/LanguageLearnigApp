package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class RegisterActivity : BaseClass() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.registerName)
        val passwordEditText = findViewById<EditText>(R.id.registerPassword)
        val registerButton = findViewById<Button>(R.id.doRegisterButton)

        registerButton.setOnClickListener {
            registerButton.isEnabled = false
            val name = nameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                postAnswer(name, password)
            }
            registerButton.isEnabled = true
        }
    }

    private fun postAnswer(username: String, password: String) {
        Log.i("API", "POSTING\nusername $username\npassword $password")

        val json = "{\"username\": \"$username\",\"password\": \"$password\"}"

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url(url + "register")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("API", "Failed to POST ${e.message}")
                postAnswer(username, password)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                Log.i("API", "Succeeded to POST $responseString")
                if ("{\"message\":\"User registered successfully\"}\n" == responseString)
                    afterPost()
            }
        }
        )
    }

    private fun afterPost() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
