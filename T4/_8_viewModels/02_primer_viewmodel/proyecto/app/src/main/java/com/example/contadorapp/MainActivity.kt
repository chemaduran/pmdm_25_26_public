package com.example.contadorapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.contadorapp.databinding.ActivityMainBinding

/**
 * MainActivity CON ViewModel
 * 
 * Ahora el contador sobrevive a la rotación porque está
 * almacenado en el ViewModel, no en la Activity.
 */
class MainActivity : AppCompatActivity() {

    // Obtener el ViewModel usando el delegado viewModels()
    // Este delegado crea el ViewModel si no existe, o lo reutiliza si ya existe
    private val viewModel: ContadorViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el valor actual del ViewModel
        actualizarContador()

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            viewModel.incrementar()
            actualizarContador()
        }

        binding.btnDecrementar.setOnClickListener {
            viewModel.decrementar()
            actualizarContador()
        }
    }

    private fun actualizarContador() {
        // Leemos el valor del ViewModel, no de una variable local
        binding.tvContador.text = viewModel.contador.toString()
    }
}
