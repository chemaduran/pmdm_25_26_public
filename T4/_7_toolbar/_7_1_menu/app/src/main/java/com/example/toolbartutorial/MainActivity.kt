package com.example.toolbartutorial

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/**
 * MainActivity - Iteración 2: Menú con acciones
 * 
 * Añadimos un menú con acciones a la Toolbar:
 * - Iconos de búsqueda y favoritos visibles
 * - Opciones de configuración y acerca de en el overflow
 * - Gestión de clics en cada item del menú
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener referencia a la Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        
        // Establecer la Toolbar como ActionBar de la Activity
        setSupportActionBar(toolbar)
        
        // Configurar título
        supportActionBar?.title = getString(R.string.main_screen_title)
    }

    /**
     * Inflar el menú de opciones
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Gestionar clics en los items del menú
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
                showToast(getString(R.string.action_settings))
                true
            }
            R.id.action_about -> {
                showToast(getString(R.string.action_about))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
