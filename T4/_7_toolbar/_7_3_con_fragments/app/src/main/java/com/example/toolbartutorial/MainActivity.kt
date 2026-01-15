package com.example.toolbartutorial

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar

/**
 * MainActivity - Iteración 4: Single Activity con Navigation Component
 * 
 * Esta es la arquitectura recomendada por Google:
 * - Una sola Activity
 * - Múltiples Fragments gestionados por Navigation Component
 * - La Toolbar se conecta automáticamente con el NavController
 * - El menú de la Toolbar (tres puntos) es global para todos los Fragments
 *
 * Ventajas:
 * - Navegación declarativa en nav_graph.xml
 * - Gestión automática del back stack
 * - Transiciones animadas configurables
 * - Safe Args para paso de datos type-safe
 * - Menú consistente en toda la aplicación
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Obtener NavController desde el NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar qué destinos son "top-level" (sin flecha atrás)
        // Solo el homeFragment es top-level, el resto mostrará flecha atrás
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment)
        )

        // Conectar la Toolbar con el NavController
        // Esto automáticamente:
        // - Actualiza el título según el destino (usando android:label del nav_graph)
        // - Muestra/oculta la flecha de navegación
        // - Gestiona el comportamiento del botón atrás
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Inflar el menú global de la Toolbar.
     * Este menú se muestra en todos los Fragments.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Gestionar los clics en los ítems del menú.
     * El menú es común para todos los Fragments.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Navegar al SettingsFragment usando Navigation Component
                navController.navigate(R.id.settingsFragment)
                true
            }
            R.id.action_about -> {
                Toast.makeText(this, getString(R.string.action_about), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Gestionar el botón de navegación en la Toolbar
     * Delegamos al NavController para que gestione la navegación
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
