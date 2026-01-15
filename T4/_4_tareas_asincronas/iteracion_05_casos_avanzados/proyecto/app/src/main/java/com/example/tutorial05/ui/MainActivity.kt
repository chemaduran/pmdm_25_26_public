package com.example.tutorial05.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tutorial05.data.model.DatosCombinados
import com.example.tutorial05.data.model.Usuario
import com.example.tutorial05.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Activity que demuestra casos avanzados de corrutinas.
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    manejarEstado(state)
                }
            }
        }
    }
    
    private fun manejarEstado(state: UiState<DatosUI>) {
        when (state) {
            is UiState.Idle -> mostrarEstadoInicial()
            is UiState.Loading -> mostrarCargando("Cargando...")
            is UiState.LoadingConInfo -> mostrarCargando(state.mensaje)
            is UiState.Success -> mostrarExito(state.data)
            is UiState.Error -> mostrarError(state.mensaje)
        }
    }
    
    private fun mostrarEstadoInicial() {
        binding.apply {
            progressBar.visibility = View.GONE
            tvEstadoCarga.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            tvResultado.text = "Selecciona una operación para probar"
            cardError.visibility = View.GONE
            btnCancelar.visibility = View.GONE
            habilitarBotones(true)
        }
    }
    
    private fun mostrarCargando(mensaje: String) {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvEstadoCarga.visibility = View.VISIBLE
            tvEstadoCarga.text = mensaje
            tvResultado.visibility = View.GONE
            cardError.visibility = View.GONE
            btnCancelar.visibility = View.VISIBLE
            habilitarBotones(false)
        }
    }
    
    private fun mostrarExito(datos: DatosUI) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvEstadoCarga.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
            cardError.visibility = View.GONE
            btnCancelar.visibility = View.GONE
            habilitarBotones(true)
            
            when (datos) {
                is DatosUI.Usuarios -> mostrarUsuarios(datos.lista)
                is DatosUI.DatosMixtos -> mostrarDatosCombinados(datos.datos)
            }
        }
    }
    
    private fun mostrarUsuarios(usuarios: List<Usuario>) {
        val texto = buildString {
            appendLine("✅ ${usuarios.size} usuarios cargados:\n")
            usuarios.forEach { usuario ->
                appendLine("👤 ${usuario.nombre}")
                appendLine("   📧 ${usuario.email}")
                appendLine()
            }
        }
        binding.tvResultado.text = texto
    }
    
    private fun mostrarDatosCombinados(datos: DatosCombinados) {
        val texto = buildString {
            appendLine("✅ Carga paralela completada en ${datos.tiempoCargaMs}ms\n")
            
            appendLine("👥 USUARIOS (${datos.usuarios.size}):")
            datos.usuarios.forEach { usuario ->
                appendLine("   • ${usuario.nombre}")
            }
            appendLine()
            
            appendLine("📦 PRODUCTOS (${datos.productos.size}):")
            datos.productos.forEach { producto ->
                appendLine("   • ${producto.nombre} - $${producto.precio}")
            }
        }
        binding.tvResultado.text = texto
    }
    
    private fun mostrarError(mensaje: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvEstadoCarga.visibility = View.GONE
            tvResultado.visibility = View.GONE
            cardError.visibility = View.VISIBLE
            tvError.text = mensaje
            btnCancelar.visibility = View.GONE
            habilitarBotones(true)
        }
    }
    
    private fun habilitarBotones(habilitar: Boolean) {
        binding.apply {
            btnNormal.isEnabled = habilitar
            btnRetry.isEnabled = habilitar
            btnTimeout.isEnabled = habilitar
            btnParalelo.isEnabled = habilitar
        }
    }
    
    private fun setupListeners() {
        binding.apply {
            btnNormal.setOnClickListener {
                viewModel.cargarNormal()
            }
            
            btnRetry.setOnClickListener {
                viewModel.cargarConRetry()
            }
            
            btnTimeout.setOnClickListener {
                viewModel.cargarConTimeout()
            }
            
            btnParalelo.setOnClickListener {
                viewModel.cargarEnParalelo()
            }
            
            btnCancelar.setOnClickListener {
                viewModel.cancelarYResetear()
            }
        }
    }
}
