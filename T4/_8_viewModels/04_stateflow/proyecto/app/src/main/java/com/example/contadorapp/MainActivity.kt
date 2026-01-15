package com.example.contadorapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.contadorapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * MainActivity con observación de StateFlow
 * 
 * Usamos coroutines para recolectar los valores del StateFlow
 * de forma segura respecto al ciclo de vida.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ContadorViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observar cambios en el contador con StateFlow
        // lifecycleScope: Coroutine scope ligado al ciclo de vida de la Activity
        lifecycleScope.launch {
            // repeatOnLifecycle: Solo recolecta cuando el lifecycle está en STARTED o superior
            // Esto evita recolectar cuando la Activity está en segundo plano
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // collectLatest: Recolecta los valores del Flow
                // Si llega un nuevo valor antes de procesar el anterior, cancela y procesa el nuevo
                viewModel.contador.collectLatest { valor ->
                    binding.tvContador.text = valor.toString()
                }
            }
        }

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            viewModel.incrementar()
        }

        binding.btnDecrementar.setOnClickListener {
            viewModel.decrementar()
        }
    }
}
