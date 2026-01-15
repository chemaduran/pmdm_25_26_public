package com.example.sensorespracticaguiada

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorespracticaguiada.databinding.ActivityCompassBinding
import kotlin.math.roundToInt

class CompassActivity : AppCompatActivity(), SensorEventListener {

	private lateinit var binding: ActivityCompassBinding
	private lateinit var sensorManager: SensorManager
	private var accelerometer: Sensor? = null
	private var magnetometer: Sensor? = null

	private val gravity = FloatArray(3)
	private val geomagnetic = FloatArray(3)
	private val rotationMatrix = FloatArray(9)
	private val orientation = FloatArray(3)

	private var currentDegree = 0f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCompassBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Configurar sensores
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

		// Verificar disponibilidad
		if (accelerometer == null || magnetometer == null) {
			binding.tvCalibration.text = "❌ Sensores no disponibles"
		}
	}

	override fun onResume() {
		super.onResume()
		accelerometer?.also { sensor ->
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
		}
		magnetometer?.also { sensor ->
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
		}
	}

	override fun onPause() {
		super.onPause()
		sensorManager.unregisterListener(this)
	}

	override fun onSensorChanged(event: SensorEvent) {
		when (event.sensor.type) {
			Sensor.TYPE_ACCELEROMETER -> event.values.copyInto(gravity)
			Sensor.TYPE_MAGNETIC_FIELD -> event.values.copyInto(geomagnetic)
		}

		// Calcular matriz de rotación y orientación
		val success = SensorManager.getRotationMatrix(
			rotationMatrix,
			null,
			gravity,
			geomagnetic
		)

		if (success) {
			SensorManager.getOrientation(rotationMatrix, orientation)

			// Azimut en radianes, convertir a grados
			val azimuthRad = orientation[0]
			var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()

			// Normalizar a 0-360
			azimuthDeg = (azimuthDeg + 360) % 360

			// Actualizar UI
			updateCompass(azimuthDeg)
		}
	}

	private fun updateCompass(degree: Float) {
		// Actualizar texto de grados
		binding.tvDegrees.text = "${degree.roundToInt()}°"

		// Actualizar dirección cardinal
		binding.tvDirection.text = getDirection(degree)

		// Animar la flecha
		val rotateAnimation = RotateAnimation(
			currentDegree,
			-degree,
			Animation.RELATIVE_TO_SELF, 0.5f,
			Animation.RELATIVE_TO_SELF, 0.5f
		).apply {
			duration = 200
			fillAfter = true
		}

		binding.ivCompass.startAnimation(rotateAnimation)
		currentDegree = -degree
	}

	private fun getDirection(degree: Float): String {
		return when (degree) {
			in 0f..22.5f, in 337.5f..360f -> "Norte"
			in 22.5f..67.5f -> "Noreste"
			in 67.5f..112.5f -> "Este"
			in 112.5f..157.5f -> "Sureste"
			in 157.5f..202.5f -> "Sur"
			in 202.5f..247.5f -> "Suroeste"
			in 247.5f..292.5f -> "Oeste"
			in 292.5f..337.5f -> "Noroeste"
			else -> "Norte"
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		when (accuracy) {
			SensorManager.SENSOR_STATUS_UNRELIABLE -> {
				binding.tvCalibration.text =
					"⚠️ Precisión baja. Calibra moviendo en forma de 8"
			}

			SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
				binding.tvCalibration.text = "Calibrando..."
			}

			SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM,
			SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
				binding.tvCalibration.text = "✓ Calibración correcta"
			}
		}
	}
}

