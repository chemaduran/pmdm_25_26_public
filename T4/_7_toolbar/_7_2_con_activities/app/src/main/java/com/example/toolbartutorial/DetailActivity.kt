package com.example.toolbartutorial

import android.os.Bundle

/**
 * DetailActivity - Pantalla de detalle
 * 
 * Hereda de BaseActivity para reutilizar la configuración de Toolbar.
 * Muestra el botón de navegación atrás.
 */
class DetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Configurar Toolbar CON botón atrás
        setupToolbar(showBackButton = true, title = getString(R.string.detail_screen_title))
    }
}
