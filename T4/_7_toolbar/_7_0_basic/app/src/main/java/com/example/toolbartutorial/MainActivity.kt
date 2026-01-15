package com.example.toolbartutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/**
 * MainActivity - Iteración 1: Toolbar básica
 * 
 * En esta primera versión, configuramos una Toolbar sencilla
 * que reemplaza a la ActionBar por defecto.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener referencia a la Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        
        // Establecer la Toolbar como ActionBar de la Activity
        setSupportActionBar(toolbar)
        
        // Opcional: Configurar título desde código
        supportActionBar?.title = getString(R.string.main_screen_title)
    }
}
