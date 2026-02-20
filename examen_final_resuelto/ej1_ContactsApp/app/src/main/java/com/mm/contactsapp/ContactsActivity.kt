package com.mm.contactsapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ContactsActivity : AppCompatActivity() {

    private lateinit var totalContactsTextView: TextView
    private lateinit var contactsStartingVowelTextView: TextView
    private lateinit var shortestContactTextView: TextView
    private lateinit var contactInput: EditText
    private lateinit var findButton: Button
    private lateinit var filterButton: Button
    private lateinit var resultText: TextView

    private lateinit var email: String
    private lateinit var contacts: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        // Obtener datos del intent
        email = intent.getStringExtra("email") ?: ""
        contacts = intent.getStringArrayListExtra("contacts") ?: emptyList()

        // Inicializar vistas
        totalContactsTextView = findViewById(R.id.totalContacts)
        contactsStartingVowelTextView = findViewById(R.id.contactsStartingVowel)
        shortestContactTextView = findViewById(R.id.shortestContact)
        contactInput = findViewById(R.id.contactInput)
        findButton = findViewById(R.id.findButton)
        filterButton = findViewById(R.id.filterButton)
        resultText = findViewById(R.id.resultText)

        // Mostrar estadísticas
        displayStatistics()

        // Configurar listeners
        findButton.setOnClickListener {
            findExactContact()
        }

        filterButton.setOnClickListener {
            filterContacts()
        }
    }

    private fun displayStatistics() {
        // 1. Número total de contactos
        val totalContacts = contacts.size
        totalContactsTextView.text = getString(R.string.total_contacts, totalContacts)

        // 2. Número de contactos que empiezan por vocal
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'á', 'é', 'í', 'ó', 'ú', 'A', 'E', 'I', 'O', 'U')
        val contactsStartingWithVowel = contacts.count { contact ->
            contact.isNotEmpty() && vowels.contains(contact.first())
        }
        contactsStartingVowelTextView.text = getString(R.string.contacts_starting_vowel, contactsStartingWithVowel)

        // 3. Contacto(s) con nombre más corto
        val minLength = contacts.minOfOrNull { it.length } ?: 0
        val shortestContacts = contacts.filter { it.length == minLength }
        shortestContactTextView.text = getString(R.string.shortest_contact, shortestContacts.joinToString(", "))
    }

    private fun findExactContact() {
        val searchName = contactInput.text.toString().trim().lowercase()

        if (searchName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show()
            return
        }

        val found = contacts.any { it.lowercase() == searchName }

        if (found) {
            resultText.text = getString(R.string.contact_found)
            Toast.makeText(this, getString(R.string.contact_found), Toast.LENGTH_SHORT).show()
        } else {
            resultText.text = getString(R.string.contact_not_found)
            Toast.makeText(this, getString(R.string.contact_not_found), Toast.LENGTH_SHORT).show()
        }

        // Guardar búsqueda en preferencias
        saveLastSearch(searchName)
    }

    private fun filterContacts() {
        val searchName = contactInput.text.toString().trim().lowercase()

        if (searchName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show()
            return
        }

        val matches = contacts.filter { it.lowercase().contains(searchName) }

        if (matches.isNotEmpty()) {
            resultText.text = "${getString(R.string.matches_found, matches.size)}\n${matches.joinToString(", ")}"
        } else {
            resultText.text = getString(R.string.no_matches)
        }

        // Guardar búsqueda en preferencias
        saveLastSearch(searchName)
    }

    private fun saveLastSearch(searchName: String) {
        try {
            val sharedPreferences = getSharedPreferences("ContactsAppPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDateTime = dateFormat.format(Date())

            editor.putString("last_email", email)
            editor.putString("last_search_datetime", currentDateTime)
            editor.putString("last_search_name", searchName)
            editor.apply()

            android.util.Log.d("ContactsActivity", "Last search saved: $email, $currentDateTime, $searchName")
        } catch (e: Exception) {
            android.util.Log.e("ContactsActivity", "Error saving last search: ${e.message}")
        }
    }
}
