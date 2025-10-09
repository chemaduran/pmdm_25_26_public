package com.mm.multi

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "CHEMA MULTI"

    override fun onCreate(savedInstanceState: Bundle?) {  //  inicialización básica: setContentView, inyección de dependencias, restaurar estado.
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón de Play
        val buttonPlay = findViewById<Button>(R.id.buttonPlay)
        buttonPlay.setOnClickListener {
            playAudio()
        }

        // Configurar el botón de Stop
        val buttonStop = findViewById<Button>(R.id.buttonStop)
        buttonStop.setOnClickListener {
            stopAudio()
        }

        // Configurar el botón para mostrar el diálogo
        val buttonDialog = findViewById<Button>(R.id.buttonDialog)
        buttonDialog.setOnClickListener {
            showConfirmationDialog()
        }
    }

    override fun onStart() { // la Activity es visible; prepara UI reactiva o listeners visibles.
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() { // la Activity está en primer plano e interactiva; reanuda sensores, cámaras o animaciones.
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() { //  la Activity pierde foco; guarda estado volátil y libera recursos pesados (cámara, grabación).
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() { // ya no es visible; detén tareas costosas asociadas a la UI.
        super.onStop()
        Log.d(TAG, "onStop()")
        // Libera los recursos de MediaPlayer cuando la actividad se detiene
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() { //  limpieza final.
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

    private fun playAudio() {  // Función para reproducir un fichero de audio desde la carpeta res/raw.
        // Detiene y libera cualquier reproducción anterior
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create( // Crea una nueva instancia de MediaPlayer con el fichero de audio
            this, R.raw.hasta_la_vista // Asegúrate de que tu fichero se llama tal cual lo muestras aquí.
        )
        mediaPlayer?.start() // Inicia la reproducción del audio
    }

    //Función para detener la reproducción del audio.
    private fun stopAudio() {
        // Detiene la reproducción del audio
        mediaPlayer?.stop()
    }

    // Muestra un diálogo de confirmación.
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        // Configurar título y mensaje
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que quieres realizar esta acción?")

        // Configurar el botón positivo (Aceptar) y su acción
        builder.setPositiveButton("Aceptar") { dialog, which ->
            // --- AQUÍ VA LA ACCIÓN QUE QUIERES EJECUTAR ---
            // Por ejemplo, mostramos un mensaje Toast
            Toast.makeText(applicationContext, "Acción confirmada.", Toast.LENGTH_SHORT).show()
            // También podrías llamar a playAudio(), stopAudio() o cualquier otra función.
            // playAudio()
        }

        // Configurar el botón negativo (Cancelar)
        builder.setNegativeButton("Cancelar") { dialog, which ->
            // Acción al cancelar (opcional, puedes dejarlo vacío si no hace nada)
            Toast.makeText(applicationContext, "Acción cancelada.", Toast.LENGTH_SHORT).show()
        }

        // Crear y mostrar el diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}