package com.example.tutorial02

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorial02.databinding.ActivityMainBinding

/**
 * Activity principal de la aplicación.
 * 
 * Observa el UiState del ViewModel y actualiza la UI
 * según el estado actual.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupObservers()
        setupListeners()
    }
    
    /**
     * Configura el observador del UiState.
     * 
     * Usamos when para manejar todos los estados posibles.
     * El compilador nos avisa si olvidamos algún estado.
     */
    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            // Manejamos cada estado posible
            when (state) {
                is UiState.Idle -> mostrarEstadoInicial()
                is UiState.Loading -> mostrarCargando()
                is UiState.Success -> mostrarUsuarios(state.data)
                is UiState.Error -> mostrarError(state.mensaje)
            }
        }
    }
    
    /**
     * Estado inicial: UI en reposo, esperando acción del usuario.
     */
    private fun mostrarEstadoInicial() {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            tvResultado.text = "Pulsa el botón para cargar usuarios"
            cardError.visibility = View.GONE
            btnCargar.isEnabled = true
            btnCargar.text = "Cargar Usuarios"
        }
    }
    
    /**
     * Estado de carga: mostramos ProgressBar y deshabilitamos acciones.
     */
    private fun mostrarCargando() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvResultado.visibility = View.GONE
            cardError.visibility = View.GONE
            btnCargar.isEnabled = false
            btnCargar.text = "Cargando..."
        }
    }
    
    /**
     * Estado de éxito: mostramos los datos obtenidos.
     */
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            cardError.visibility = View.GONE
            btnCargar.isEnabled = true
            btnCargar.text = "Recargar Usuarios"
            
            val texto = usuarios.joinToString("\n\n") { usuario ->
                "👤 ${usuario.nombre}\n   📧 ${usuario.email}"
            }
            tvResultado.text = texto
        }
    }
    
    /**
     * Estado de error: mostramos el mensaje de error.
     */
    private fun mostrarError(mensaje: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.GONE
            cardError.visibility = View.VISIBLE
            tvError.text = mensaje
            btnCargar.isEnabled = true
            btnCargar.text = "Reintentar"
        }
    }
    
    private fun setupListeners() {
        binding.btnCargar.setOnClickListener {
            viewModel.cargarUsuarios()
        }
    }
}
