package com.example.sensorespracticaguiada

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorespracticaguiada.databinding.ActivityAccelerometerBinding
import kotlin.math.sqrt

class AccelerometerActivity : AppCompatActivity(), SensorEventListener {

	private lateinit var binding: ActivityAccelerometerBinding
	private lateinit var sensorManager: SensorManager
	private var accelerometer: Sensor? = null

	// Variables para detección de shake
	private var lastShakeTime = 0L
	private var shakeCount = 0
	private val shakeThreshold = 15f  // Umbral de aceleración
	private val shakeCooldown = 500L  // Tiempo mínimo entre sacudidas (ms)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAccelerometerBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Configurar SensorManager
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

		// Verificar disponibilidad
		if (accelerometer == null) {
			binding.tvShakeStatus.text = "❌ Acelerómetro no disponible"
		}
	}

	override fun onResume() {
		super.onResume()
		accelerometer?.also { sensor ->
			sensorManager.registerListener(
				this,
				sensor,
				SensorManager.SENSOR_DELAY_UI
			)
		}
	}

	override fun onPause() {
		super.onPause()
		sensorManager.unregisterListener(this)
	}

	override fun onSensorChanged(event: SensorEvent) {
		if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
			val x = event.values[0]
			val y = event.values[1]
			val z = event.values[2]

			// Actualizar UI
			binding.tvXAxis.text = "X: %.2f m/s²".format(x)
			binding.tvYAxis.text = "Y: %.2f m/s²".format(y)
			binding.tvZAxis.text = "Z: %.2f m/s²".format(z)

			// Calcular magnitud
			val magnitude = sqrt(x * x + y * y + z * z)
			binding.tvMagnitude.text = "Magnitud: %.2f m/s²".format(magnitude)

			// Detectar sacudida
			detectShake(magnitude)
		}
	}

	private fun detectShake(acceleration: Float) {
		if (acceleration > shakeThreshold) {
			val currentTime = System.currentTimeMillis()

			// Evitar múltiples detecciones
			if (currentTime - lastShakeTime > shakeCooldown) {
				shakeCount++
				lastShakeTime = currentTime

				// Actualizar UI
				binding.tvShakeStatus.text = "🎉 ¡Sacudida detectada!"
				binding.tvShakeCount.text = "Sacudidas: $shakeCount"

				// Feedback visual (opcional)
				binding.cardAcceleration.animate()
					.scaleX(1.1f)
					.scaleY(1.1f)
					.setDuration(100)
					.withEndAction {
						binding.cardAcceleration.animate()
							.scaleX(1.0f)
							.scaleY(1.0f)
							.setDuration(100)
							.start()
					}
					.start()

				// Resetear mensaje después de 1 segundo
				binding.tvShakeStatus.postDelayed({
					binding.tvShakeStatus.text = "Agita el dispositivo"
				}, 1000)
			}
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		// No se requiere acción
	}
}

