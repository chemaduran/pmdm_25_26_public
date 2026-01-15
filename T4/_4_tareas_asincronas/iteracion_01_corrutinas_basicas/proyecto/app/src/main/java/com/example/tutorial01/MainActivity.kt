package com.example.tutorial01

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorial01.databinding.ActivityMainBinding

/**
 * Activity principal de la aplicación.
 * 
 * Responsabilidades:
 * - Mostrar la UI
 * - Observar los cambios en el ViewModel
 * - Reaccionar a las acciones del usuario
 * 
 * NO debe contener lógica de negocio ni llamadas a APIs.
 */
class MainActivity : AppCompatActivity() {
    
    // View Binding para acceder a las vistas de forma segura
    private lateinit var binding: ActivityMainBinding
    
    // Obtenemos el ViewModel usando el delegado by viewModels()
    // Esto crea el ViewModel y lo asocia al ciclo de vida de la Activity
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inflamos el layout usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configuramos los observadores de LiveData
        setupObservers()
        
        // Configuramos los listeners de los botones
        setupListeners()
    }
    
    /**
     * Configura los observadores de LiveData.
     * 
     * Cada vez que el valor de un LiveData cambia,
     * el observador recibe el nuevo valor y actualiza la UI.
     */
    private fun setupObservers() {
        // Observamos la lista de usuarios
        viewModel.usuarios.observe(this) { usuarios ->
            mostrarUsuarios(usuarios)
        }
        
        // Observamos el estado de carga
        viewModel.cargando.observe(this) { estaCargando ->
            // Mostramos u ocultamos el ProgressBar
            binding.progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
            
            // Deshabilitamos el botón mientras carga
            binding.btnCargar.isEnabled = !estaCargando
            
            // Cambiamos el texto del botón
            binding.btnCargar.text = if (estaCargando) "Cargando..." else "Cargar Usuarios"
        }
    }
    
    /**
     * Configura los listeners de los botones.
     */
    private fun setupListeners() {
        binding.btnCargar.setOnClickListener {
            // Pedimos al ViewModel que cargue los usuarios
            viewModel.cargarUsuarios()
        }
    }
    
    /**
     * Muestra la lista de usuarios en el TextView.
     * 
     * @param usuarios Lista de usuarios a mostrar
     */
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        val texto = usuarios.joinToString("\n\n") { usuario ->
            "👤 ${usuario.nombre}\n   📧 ${usuario.email}"
        }
        binding.tvResultado.text = texto
    }
}
