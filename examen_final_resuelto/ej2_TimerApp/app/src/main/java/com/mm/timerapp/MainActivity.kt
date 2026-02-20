package com.mm.timerapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Vistas
    private lateinit var tvInfo: TextView
    private lateinit var tvTiempoRestante: TextView
    private lateinit var ivFase: ImageView
    private lateinit var tvFaseActual: TextView
    private lateinit var tvProgreso: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnIniciar: Button
    private lateinit var tvResultadoFinal: TextView

    // Configuración del temporizador
    private val tiempoTotal = 20              // Tiempo total en segundos
    private val checkpoint25 = tiempoTotal * 75 / 100   // 25% completado = 75% restante
    private val checkpoint50 = tiempoTotal * 50 / 100   // 50% completado = 50% restante
    private val checkpoint75 = tiempoTotal * 25 / 100   // 75% completado = 25% restante

    // Variables de estado
    private var tiempoRestante = tiempoTotal
    private var fechaInicio: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        tvInfo = findViewById(R.id.tvInfo)
        tvTiempoRestante = findViewById(R.id.tvTiempoRestante)
        ivFase = findViewById(R.id.ivFase)
        tvFaseActual = findViewById(R.id.tvFaseActual)
        tvProgreso = findViewById(R.id.tvProgreso)
        progressBar = findViewById(R.id.progressBar)
        btnIniciar = findViewById(R.id.btnIniciar)
        tvResultadoFinal = findViewById(R.id.tvResultadoFinal)

        // Mostrar información inicial
        tvInfo.text = "Cuenta atrás: $tiempoTotal segundos\nCheckpoints: 25%, 50%, 75%"
        tvProgreso.text = "Progreso: 0%"
        progressBar.max = tiempoTotal
        progressBar.progress = 0

        // Configurar botón de inicio
        btnIniciar.setOnClickListener {
            iniciarCuentaAtras()
        }
    }

    /**
     * Inicia la cuenta atrás usando corrutinas
     */
    private fun iniciarCuentaAtras() {
        // Reiniciar estado
        tiempoRestante = tiempoTotal
        progressBar.progress = 0
        tvResultadoFinal.visibility = View.GONE
        btnIniciar.isEnabled = false
        ivFase.setImageResource(R.drawable.ic_fase_inicio)
        tvFaseActual.text = "Fase: Inicio"

        // Guardar fecha de inicio
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        fechaInicio = sdf.format(Date())

        Log.d("TimerApp", "Cuenta atrás iniciada: $fechaInicio")

        // Iniciar tarea asíncrona con corrutinas
        lifecycleScope.launch {
            ejecutarCuentaAtras()
        }
    }

    /**
     * Ejecuta la cuenta atrás completa usando corrutinas
     * lifecycleScope.launch asegura que la corrutina se cancele si la Activity se destruye
     */
    private suspend fun ejecutarCuentaAtras() {
        while (tiempoRestante > 0) {
            // Actualizar UI
            tvTiempoRestante.text = tiempoRestante.toString()
            
            val porcentajeCompletado = ((tiempoTotal - tiempoRestante) * 100) / tiempoTotal
            tvProgreso.text = "Progreso: $porcentajeCompletado%"
            progressBar.progress = tiempoTotal - tiempoRestante

            // Determinar fase y mostrar imagen correspondiente
            actualizarFase(tiempoRestante)

            Log.d("TimerApp", "Tiempo restante: $tiempoRestante - Progreso: $porcentajeCompletado%")

            // Esperar 1 segundo
            delay(1000)
            tiempoRestante--
        }

        // Cuenta atrás completada
        tvTiempoRestante.text = "0"
        progressBar.progress = tiempoTotal
        tvProgreso.text = "Progreso: 100%"
        ivFase.setImageResource(R.drawable.ic_fase_final)
        tvFaseActual.text = "Fase: ¡Completado!"
        
        finalizarCuentaAtras()
    }

    /**
     * Actualiza la fase visual según el tiempo restante
     */
    private fun actualizarFase(tiempo: Int) {
        when {
            tiempo > checkpoint25 -> {
                // Menos del 25% completado
                ivFase.setImageResource(R.drawable.ic_fase_inicio)
                tvFaseActual.text = "Fase: Inicio (0-25%)"
            }
            tiempo > checkpoint50 -> {
                // Entre 25% y 50% completado
                ivFase.setImageResource(R.drawable.ic_fase_25)
                tvFaseActual.text = "Fase: Cuarto (25-50%)"
            }
            tiempo > checkpoint75 -> {
                // Entre 50% y 75% completado
                ivFase.setImageResource(R.drawable.ic_fase_50)
                tvFaseActual.text = "Fase: Mitad (50-75%)"
            }
            else -> {
                // Más del 75% completado
                ivFase.setImageResource(R.drawable.ic_fase_75)
                tvFaseActual.text = "Fase: Final (75-100%)"
            }
        }
    }

    /**
     * Finaliza la cuenta atrás y guarda el resultado en memoria interna
     */
    private fun finalizarCuentaAtras() {
        btnIniciar.isEnabled = true

        // Mostrar resultado final
        val mensaje = "¡Cuenta atrás completada!\nTiempo total: $tiempoTotal segundos"
        tvResultadoFinal.text = mensaje
        tvResultadoFinal.visibility = View.VISIBLE

        Log.d("TimerApp", "Cuenta atrás finalizada")

        // Guardar en fichero de memoria interna
        guardarResultado()
    }

    /**
     * Guarda el resultado en un fichero de memoria interna
     */
    private fun guardarResultado() {
        try {
            val nombreFichero = "timer_log.txt"
            val contenido = "Inicio: $fechaInicio | Tiempo total: $tiempoTotal segundos\n"

            // Abrir fichero en modo append
            val fos: FileOutputStream = openFileOutput(nombreFichero, MODE_APPEND)
            fos.write(contenido.toByteArray())
            fos.close()

            Toast.makeText(this, "Resultado guardado correctamente", Toast.LENGTH_SHORT).show()
            Log.d("TimerApp", "Resultado guardado en $nombreFichero")

        } catch (e: Exception) {
            Log.e("TimerApp", "Error al guardar resultado: ${e.message}")
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
