package com.example.contadorapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.contadorapp.databinding.ActivityMainBinding

/**
 * MainActivity con observación de LiveData
 * 
 * Ya no necesitamos llamar a actualizarContador() manualmente.
 * La UI se actualiza automáticamente cuando cambia el LiveData.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ContadorViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observar cambios en el contador
        // Cuando el valor cambie, se ejecutará automáticamente el bloque
        // El parámetro 'this' es el LifecycleOwner (la Activity)
        viewModel.contador.observe(this) { valor ->
            binding.tvContador.text = valor.toString()
        }

        // Configurar los botones
        // ¡Ya no necesitamos llamar a actualizarContador()!
        binding.btnIncrementar.setOnClickListener {
            viewModel.incrementar()
            // La UI se actualiza automáticamente gracias a LiveData
        }

        binding.btnDecrementar.setOnClickListener {
            viewModel.decrementar()
            // La UI se actualiza automáticamente gracias a LiveData
        }
    }
}
