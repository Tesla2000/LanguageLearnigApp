package com.example.myapplication

import android.os.Bundle


open class LoggedClass : BaseClass() {
    protected lateinit var username: String
    protected lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = intent.getStringExtra("username")!!
        token = intent.getStringExtra("token")!!
    }
}