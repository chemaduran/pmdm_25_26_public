package com.example.sensorespracticaguiada

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sensorespracticaguiada.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var sensorManager: SensorManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Obtener el SensorManager
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

		// Configurar RecyclerView
		setupRecyclerView()

		// Configurar botones
		setupButtons()
	}

	private fun setupRecyclerView() {
		val allSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

		binding.rvSensors.apply {
			layoutManager = LinearLayoutManager(this@MainActivity)
			adapter = SensorAdapter(allSensors)
		}

		binding.tvSubtitle.text = "Sensores disponibles: ${allSensors.size}"
	}

	private fun setupButtons() {
		binding.btnAccelerometer.setOnClickListener {
			startActivity(Intent(this, AccelerometerActivity::class.java))
		}

		binding.btnCompass.setOnClickListener {
			startActivity(Intent(this, CompassActivity::class.java))
		}

		binding.btnLight.setOnClickListener {
			startActivity(Intent(this, LightSensorActivity::class.java))
		}
	}
}

