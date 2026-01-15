package com.example.toolbartutorial

import android.os.Bundle

/**
 * SettingsActivity - Pantalla de configuración
 * 
 * Hereda de BaseActivity para reutilizar la configuración de Toolbar.
 * Muestra el botón de navegación atrás.
 */
class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Configurar Toolbar CON botón atrás
        setupToolbar(showBackButton = true, title = getString(R.string.settings_screen_title))
    }
}
