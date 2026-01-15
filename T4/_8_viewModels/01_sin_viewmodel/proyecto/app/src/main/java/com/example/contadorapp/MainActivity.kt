package com.example.contadorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.contadorapp.databinding.ActivityMainBinding

/**
 * MainActivity SIN ViewModel
 * 
 * PROBLEMA: Al rotar el dispositivo, el contador se pierde porque
 * la Activity se destruye y se vuelve a crear.
 */
class MainActivity : AppCompatActivity() {

    // Variable que guarda el contador
    // ⚠️ PROBLEMA: Esta variable vive en la Activity
    // Cuando la Activity se destruye, esta variable desaparece
    private var contador = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el valor inicial
        actualizarContador()

        // Configurar los botones
        binding.btnIncrementar.setOnClickListener {
            contador++
            actualizarContador()
        }

        binding.btnDecrementar.setOnClickListener {
            contador--
            actualizarContador()
        }
    }

    private fun actualizarContador() {
        binding.tvContador.text = contador.toString()
    }
}
