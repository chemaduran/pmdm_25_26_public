package com.mm.sensores

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class MainActivity : AppCompatActivity(), SensorEventListener, View.OnClickListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var luxometro: Sensor? = null
    private lateinit var layout: View
    private lateinit var ejeX: TextView
    private lateinit var ejeY: TextView
    private lateinit var ejeZ: TextView
    private lateinit var time: TextView
    private lateinit var start: Button
    private lateinit var stop: Button
    private val LOGTAG = "PMDM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Objetos del layout
        // Asociación de eventos escuchadores

        // Inicio por defecto
        stop()
    }

    override fun onClick(view: View?) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    fun start() {
        // Obtener disponibilidad del sensor
        // Valorar disponibilidad del sensor
    }

    fun stop() {
//        sensorManager.unregisterListener(this)
    }

    fun formatearValor(num: Float): String {
        return (floor(num * 100) / 100).toString()
    }

    fun hora(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}
