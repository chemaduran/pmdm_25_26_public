package com.example.toolbartutorial

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/**
 * Activity base que proporciona configuración común de Toolbar.
 * Todas las Activities que necesiten Toolbar deben heredar de esta clase.
 * 
 * Este patrón reduce la duplicación de código y asegura consistencia
 * en toda la aplicación.
 *
 * La Toolbar es accesible desde todas las Activities hijas a través de
 * la propiedad [toolbar], permitiendo personalización adicional.
 *
 * El menú de opciones (Configuración, Acerca de, etc.) está disponible
 * en todas las Activities que hereden de esta clase.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Toolbar accesible desde las clases hijas.
     * Se inicializa en [setupToolbar].
     * Permite acceso para personalización adicional (cambiar título, iconos, etc.)
     */
    protected var toolbar: MaterialToolbar? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Configura la Toolbar.
     * Llamar después de setContentView en las Activities hijas.
     * 
     * @param showBackButton true para mostrar el botón de navegación atrás
     * @param title título a mostrar en la Toolbar
     */
    protected fun setupToolbar(showBackButton: Boolean = false, title: String = "") {
        toolbar = findViewById(R.id.toolbar)
        
        toolbar?.let {
            setSupportActionBar(it)
            
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(showBackButton)
                setDisplayShowHomeEnabled(showBackButton)
                
                if (title.isNotEmpty()) {
                    setTitle(title)
                }
            }
        }
    }

    /**
     * Actualiza el título de la Toolbar.
     * Puede llamarse en cualquier momento después de [setupToolbar].
     *
     * @param title nuevo título a mostrar
     */
    protected fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    /**
     * Muestra u oculta el botón de navegación atrás.
     * Puede llamarse en cualquier momento después de [setupToolbar].
     *
     * @param show true para mostrar, false para ocultar
     */
    protected fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
        supportActionBar?.setDisplayShowHomeEnabled(show)
        if (!show) {
            toolbar?.navigationIcon = null
        }
    }

    /**
     * Inflar el menú de opciones común para todas las Activities.
     * Las Activities hijas pueden sobrescribir este método si necesitan
     * un menú diferente, pero deben llamar a super para mantener
     * los items comunes.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Gestionar clics en los items del menú.
     * Las Activities hijas pueden sobrescribir este método para añadir
     * comportamiento adicional, pero deben llamar a super para mantener
     * el comportamiento de los items comunes.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showToast(getString(R.string.action_search))
                true
            }
            R.id.action_favorite -> {
                showToast(getString(R.string.action_favorite))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                showToast(getString(R.string.action_about))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Muestra un Toast con el mensaje indicado.
     * Método helper disponible para todas las Activities hijas.
     */
    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Gestionar el clic en el botón de navegación (flecha atrás)
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
