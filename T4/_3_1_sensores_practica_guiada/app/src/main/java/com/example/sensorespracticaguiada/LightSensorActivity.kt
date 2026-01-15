package com.example.sensorespracticaguiada

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorespracticaguiada.databinding.ActivityLightSensorBinding

class LightSensorActivity : AppCompatActivity(), SensorEventListener {

	private lateinit var binding: ActivityLightSensorBinding
	private lateinit var sensorManager: SensorManager
	private var lightSensor: Sensor? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLightSensorBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Configurar sensor
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

		// Verificar disponibilidad
		if (lightSensor == null) {
			binding.tvLightStatus.text = "❌ Sensor de luz no disponible"
			binding.tvInfo.text = "Este dispositivo no tiene sensor de luz ambiental"
		}
	}

	override fun onResume() {
		super.onResume()
		lightSensor?.also { sensor ->
			sensorManager.registerListener(
				this,
				sensor,
				SensorManager.SENSOR_DELAY_NORMAL
			)
		}
	}

	override fun onPause() {
		super.onPause()
		sensorManager.unregisterListener(this)
	}

	override fun onSensorChanged(event: SensorEvent) {
		if (event.sensor.type == Sensor.TYPE_LIGHT) {
			val lux = event.values[0]
			updateUI(lux)
		}
	}

	private fun updateUI(lux: Float) {
		// Actualizar valor
		binding.tvLightValue.text = "${lux.toInt()} lux"

		// Actualizar barra de progreso (limitado a 10000 lux)
		val progress = lux.coerceAtMost(10000f).toInt()
		binding.progressBar.progress = progress

		// Determinar estado y cambiar colores
		val (status, backgroundColor, textColor) = when {
			lux < 10 -> Triple("Muy oscuro", "#000000", "#FFFFFF")
			lux < 50 -> Triple("Oscuro", "#1A1A1A", "#FFFFFF")
			lux < 200 -> Triple("Tenue", "#424242", "#FFFFFF")
			lux < 400 -> Triple("Interior", "#757575", "#FFFFFF")
			lux < 1000 -> Triple("Iluminado", "#BDBDBD", "#000000")
			lux < 5000 -> Triple("Muy iluminado", "#E0E0E0", "#000000")
			else -> Triple("Luz solar", "#FFFFFF", "#000000")
		}

		binding.tvLightStatus.text = status
		binding.rootLayout.setBackgroundColor(Color.parseColor(backgroundColor))
		binding.tvTitle.setTextColor(Color.parseColor(textColor))
		binding.tvLightValue.setTextColor(Color.parseColor(textColor))
		binding.tvLightStatus.setTextColor(Color.parseColor(textColor))
		binding.tvInfo.setTextColor(Color.parseColor(textColor))
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		// No se requiere acción
	}
}

