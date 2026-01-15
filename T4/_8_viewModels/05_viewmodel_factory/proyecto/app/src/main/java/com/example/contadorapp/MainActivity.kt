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
 * MainActivity que usa un ViewModel con parámetros.
 * 
 * Usamos la Factory para crear el ViewModel con un valor inicial personalizado.
 */
class MainActivity : AppCompatActivity() {

    // Usamos la Factory para pasar el valor inicial al ViewModel
    // El contador empezará en 10 en lugar de 0
    private val viewModel: ContadorViewModel by viewModels {
        ContadorViewModel.Factory(valorInicial = 10)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observar cambios en el contador
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
