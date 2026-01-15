package com.example.tutorial03.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.tutorial03.data.model.Usuario
import com.example.tutorial03.databinding.ActivityMainBinding

/**
 * Activity principal de la aplicación.
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
    
    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Idle -> mostrarEstadoInicial()
                is UiState.Loading -> mostrarCargando()
                is UiState.Success -> mostrarUsuarios(state.data)
                is UiState.Error -> mostrarError(state.mensaje)
            }
        }
    }
    
    private fun mostrarEstadoInicial() {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            tvResultado.text = "Pulsa el botón para cargar usuarios"
            cardError.visibility = View.GONE
            btnCargar.isEnabled = true
        }
    }
    
    private fun mostrarCargando() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvResultado.visibility = View.GONE
            cardError.visibility = View.GONE
            btnCargar.isEnabled = false
        }
    }
    
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            cardError.visibility = View.GONE
            btnCargar.isEnabled = true
            
            if (usuarios.isEmpty()) {
                tvResultado.text = "No se encontraron usuarios"
            } else {
                val texto = usuarios.joinToString("\n\n") { usuario ->
                    "👤 ${usuario.nombre}\n   📧 ${usuario.email}"
                }
                tvResultado.text = texto
            }
        }
    }
    
    private fun mostrarError(mensaje: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.GONE
            cardError.visibility = View.VISIBLE
            tvError.text = mensaje
            btnCargar.isEnabled = true
        }
    }
    
    private fun setupListeners() {
        binding.btnCargar.setOnClickListener {
            viewModel.cargarUsuarios()
        }
        
        // Búsqueda cuando el usuario escribe
        binding.etBuscar.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            if (query.length >= 2 || query.isEmpty()) {
                viewModel.buscarUsuarios(query)
            }
        }
    }
}
