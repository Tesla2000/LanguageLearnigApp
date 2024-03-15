package com.example.loginexample

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.registerName)
        val emailEditText = findViewById<EditText>(R.id.registerEmail)
        val passwordEditText = findViewById<EditText>(R.id.registerPassword)
        val registerButton = findViewById<Button>(R.id.doRegisterButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                // Here, you'd typically handle the actual registration logic, such as saving the user data
                Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show()

                // Optionally, navigate back to the login screen or main activity after registration
                finish()
            }
        }
    }
}
