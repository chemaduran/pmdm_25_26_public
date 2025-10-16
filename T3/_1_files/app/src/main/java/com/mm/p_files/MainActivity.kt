package com.mm.p_files

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rutaApp: File = this.getFilesDir();
        Log.d("Ruta", rutaApp.getAbsolutePath());

        escribirAlmacenamientoExterno()

    }

    fun abrirFicheroEscritura() {
        // Abrir un fichero en modo escritura del flujo (privado a la aplicación)
        val fos: FileOutputStream = this.openFileOutput("archivo.txt", Context.MODE_PRIVATE)

        // Escritura del flujo
        fos.use {
            it.write("Contenido del fichero\n".toByteArray())
        }

        // Alternativa: Escritura del flujo usando la función de extensión writer directamente al FileOutputStream
//        this.openFileOutput("archivo.txt", Context.MODE_PRIVATE).writer().use { writer ->
//            writer.write("Contenido del fichero\n")
//        }
    }

    fun abrirFicheroLectura() {
        // Abrir un fichero en modo lectura del flujo
        val fis: FileInputStream = this.openFileInput("archivo.txt")

        // Lectura del flujo
        var contenidoLeido = ""
        fis.use {
            contenidoLeido = it.bufferedReader().readLine()
        }
        Log.d("Lectura", "Contenido leído: $contenidoLeido")
    }

    fun existeFichero(filename: String): Boolean {
        val file = this.getFileStreamPath(filename)
        return file.exists()
    }

    fun escribirAlmacenamientoExterno() {
        val file = File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "mi_fichero.txt")
        try {
            file.writeText("Hola, Scoped Storage!")
        } catch (e: IOException) {
            Log.e("FILE I/O", "Error al escribir el fichero: ${e.message}" )
        }

    }

    fun accederFicheroRecursos() {
//        val inputStream = resources.openRawResource(R.raw.mi_fichero)
    }
}