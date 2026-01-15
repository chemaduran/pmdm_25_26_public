package com.example.sensorespracticaguiada

import android.hardware.Sensor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sensorespracticaguiada.databinding.ItemSensorBinding

class SensorAdapter(private val sensors: List<Sensor>) :
	RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

	class SensorViewHolder(val binding: ItemSensorBinding) :
		RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
		val binding = ItemSensorBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return SensorViewHolder(binding)
	}

	override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
		val sensor = sensors[position]
		holder.binding.apply {
			tvSensorName.text = sensor.name
			tvSensorType.text = "Tipo: ${sensor.type} - ${getSensorTypeName(sensor.type)}"
			tvSensorVendor.text = "Fabricante: ${sensor.vendor}"
		}
	}

	override fun getItemCount() = sensors.size

	private fun getSensorTypeName(type: Int): String {
		return when (type) {
			Sensor.TYPE_ACCELEROMETER -> "Acelerómetro"
			Sensor.TYPE_MAGNETIC_FIELD -> "Campo magnético"
			Sensor.TYPE_GYROSCOPE -> "Giroscopio"
			Sensor.TYPE_LIGHT -> "Luz"
			Sensor.TYPE_PRESSURE -> "Presión"
			Sensor.TYPE_PROXIMITY -> "Proximidad"
			Sensor.TYPE_GRAVITY -> "Gravedad"
			Sensor.TYPE_LINEAR_ACCELERATION -> "Aceleración lineal"
			Sensor.TYPE_ROTATION_VECTOR -> "Vector de rotación"
			Sensor.TYPE_RELATIVE_HUMIDITY -> "Humedad"
			Sensor.TYPE_AMBIENT_TEMPERATURE -> "Temperatura"
			Sensor.TYPE_STEP_COUNTER -> "Contador de pasos"
			Sensor.TYPE_STEP_DETECTOR -> "Detector de pasos"
			else -> "Otro ($type)"
		}
	}
}

