package com.example.tutorial04.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tutorial04.data.model.Usuario
import com.example.tutorial04.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Activity que observa StateFlow usando repeatOnLifecycle.
 * 
 * repeatOnLifecycle es la forma correcta de observar Flows en Activities:
 * - Inicia la colección cuando el lifecycle llega al estado especificado
 * - Cancela la colección cuando baja del estado
 * - Evita memory leaks y crashes
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
     * Configura los observadores de StateFlow.
     * 
     * Usamos lifecycleScope.launch + repeatOnLifecycle para:
     * - Observar de forma segura respecto al lifecycle
     * - Cancelar automáticamente cuando la Activity no está visible
     * - Reanudar cuando vuelve a estar visible
     */
    private fun setupObservers() {
        lifecycleScope.launch {
            // repeatOnLifecycle suspende la corrutina cuando el lifecycle
            // baja del estado especificado, y la reanuda cuando vuelve
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Podemos lanzar múltiples colecciones en paralelo
                launch {
                    viewModel.uiState.collect { state ->
                        manejarEstado(state)
                    }
                }
                
                launch {
                    viewModel.queryBusqueda.collect { query ->
                        // Podemos observar la query si necesitamos
                        // Por ejemplo, para mostrar "Buscando: $query"
                    }
                }
            }
        }
    }
    
    private fun manejarEstado(state: UiState<List<Usuario>>) {
        when (state) {
            is UiState.Idle -> mostrarEstadoInicial()
            is UiState.Loading -> mostrarCargando()
            is UiState.Success -> mostrarUsuarios(state.data)
            is UiState.Error -> mostrarError(state.mensaje)
        }
    }
    
    private fun mostrarEstadoInicial() {
        binding.apply {
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            tvResultado.text = "Escribe para buscar usuarios"
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
                tvResultado.text = "No se encontraron usuarios 🔍"
            } else {
                val texto = buildString {
                    appendLine("📋 ${usuarios.size} usuario(s) encontrado(s):\n")
                    usuarios.forEach { usuario ->
                        appendLine("👤 ${usuario.nombre}")
                        appendLine("   📧 ${usuario.email}")
                        appendLine()
                    }
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
        // Botón para cargar todos
        binding.btnCargar.setOnClickListener {
            binding.etBuscar.setText("")
            viewModel.cargarTodosLosUsuarios()
        }
        
        // Campo de búsqueda
        // El ViewModel se encarga del debounce
        binding.etBuscar.doAfterTextChanged { text ->
            viewModel.actualizarBusqueda(text?.toString() ?: "")
        }
    }
}
