package com.example.myapplication

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

open class BaseClass : AppCompatActivity() {
    protected val url = "http://192.168.0.8:5000/"
//    protected val url = "https://tesla2000.pythonanywhere.com/"


    protected fun getLanguages(token: String, username: String) {
        Log.i("API", "GETTING lessons")


        val request = Request.Builder()
            .url(url + "lessons")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("API", "Failed to POST ${e.message}")
                getLanguages(token, username)

            }

            override fun onResponse(call: Call, response: Response) {
                val stringResponse = response.body?.string()
                Log.i("API", "Succeeded to GET $stringResponse")
                moveToMain(token, username,
                    stringResponse!!.split(';') as ArrayList<String>
                )
            }
        }
        )
    }

    private fun moveToMain(token: String, username: String, languages: ArrayList<String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("token", token)
        intent.putExtra("languages", languages)
        startActivity(intent)
        finish()
    }
}