package com.example.toolbartutorial

import android.content.Intent
import android.os.Bundle
import android.widget.Button

/**
 * MainActivity - Iteración 3: Múltiples Activities
 * 
 * Ahora hereda de BaseActivity para reutilizar la configuración de Toolbar.
 * La pantalla principal NO muestra botón atrás.
 * Incluye navegación a DetailActivity y SettingsActivity.
 *
 * El menú de opciones se hereda de BaseActivity, por lo que
 * Configuración y Acerca de funcionan automáticamente.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar Toolbar SIN botón atrás (es la pantalla principal)
        setupToolbar(showBackButton = false, title = getString(R.string.app_name))

        // Quitar el icono de navegación para la pantalla principal
        toolbar?.navigationIcon = null

        // Navegación a otras Activities
        findViewById<Button>(R.id.btnGoToDetail).setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        findViewById<Button>(R.id.btnGoToSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
