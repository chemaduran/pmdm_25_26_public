package com.mm.contactsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var pinEditText: EditText
    private lateinit var loginButton: Button

    private var attemptCount = 0
    private val maxAttempts = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Copiar contacts.csv desde assets a memoria interna
        copyContactsFileToInternalStorage()

        emailEditText = findViewById(R.id.email)
        pinEditText = findViewById(R.id.pin)
        loginButton = findViewById(R.id.button)

        loginButton.setOnClickListener {
            handleLogin()
        }
    }

    private fun copyContactsFileToInternalStorage() {
        try {
            val fileName = "contacts.csv"
            val file = File(filesDir, fileName)

            // Solo copiar si no existe
            if (!file.exists()) {
                // Leer desde assets
                val inputStream = assets.open(fileName)
                val outputStream = openFileOutput(fileName, MODE_PRIVATE)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error copying file: ${e.message}")
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val pin = pinEditText.text.toString().trim()

        if (email.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val userContacts = authenticateUser(email, pin)

        if (userContacts != null) {
            // Login exitoso
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("email", email)
            intent.putStringArrayListExtra("contacts", ArrayList(userContacts))
            startActivity(intent)
        } else {
            // Login fallido
            attemptCount++
            Toast.makeText(
                this,
                getString(R.string.error_credentials),
                Toast.LENGTH_SHORT
            ).show()

            if (attemptCount >= maxAttempts) {
                loginButton.isEnabled = false
                Toast.makeText(
                    this,
                    getString(R.string.error_max_attempts),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun authenticateUser(email: String, pin: String): List<String>? {
        try {
            val file = File(filesDir, "contacts.csv")
            if (!file.exists()) {
                android.util.Log.e("MainActivity", "contacts.csv not found in internal storage")
                return null
            }

            file.useLines { lines ->
                for (line in lines) {
                    val parts = line.split(":")
                    if (parts.size == 2) {
                        val userEmail = parts[0]
                        val rest = parts[1].split("@")
                        if (rest.size == 2) {
                            val userPin = rest[0]
                            val contactsString = rest[1]

                            if (userEmail == email && userPin == pin) {
                                return contactsString.split(",").map { it.trim() }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error reading file: ${e.message}")
        }

        return null
    }
}
