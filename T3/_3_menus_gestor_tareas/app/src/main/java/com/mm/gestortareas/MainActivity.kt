package com.mm.gestortareas

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
	private lateinit var listViewTareas: ListView
	private lateinit var tareas: MutableList<String>
	private lateinit var adapter: TareaAdapter
	private var posicionSeleccionada: Int = -1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Inicializar vistas
		listViewTareas = findViewById(R.id.listViewTareas)

		// Crear lista de tareas de ejemplo
		tareas = mutableListOf(
			"Comprar leche", "Estudiar Kotlin", "Hacer ejercicio"
		)

		// Usar el adaptador personalizado
		adapter = TareaAdapter(this, tareas) {
			adapter.notifyDataSetChanged()
		}
		listViewTareas.adapter = adapter

		// Registrar el ListView para el menú contextual
		registerForContextMenu(listViewTareas)
	}

	// Crear menú contextual
	override fun onCreateContextMenu(
		menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
	) {
		super.onCreateContextMenu(menu, v, menuInfo)

		if (v?.id == R.id.listViewTareas) {
			menuInflater.inflate(R.menu.menu_contextual, menu)

			// Obtener la posición del item seleccionado
			val info = menuInfo as? android.widget.AdapterView.AdapterContextMenuInfo
			posicionSeleccionada = info?.position ?: -1
		}
	}

	// Manejar selección del menú contextual
	override fun onContextItemSelected(item: MenuItem): Boolean {
		if (posicionSeleccionada == -1) return super.onContextItemSelected(item)

		return when (item.itemId) {
			R.id.ctx_editar -> {
				editarTarea(posicionSeleccionada)
				true
			}

			R.id.ctx_eliminar -> {
				eliminarTarea(posicionSeleccionada)
				true
			}

			R.id.ctx_compartir -> {
				compartirTarea(posicionSeleccionada)
				true
			}

			else -> super.onContextItemSelected(item)
		}
	}

	private fun editarTarea(posicion: Int) {
		val tareaActual = tareas[posicion]

		val builder = AlertDialog.Builder(this)
		builder.setTitle("Editar Tarea")

		val input = EditText(this)
		input.setText(tareaActual)
		builder.setView(input)

		builder.setPositiveButton("Guardar") { dialog, _ ->
			val tareaEditada = input.text.toString()
			if (tareaEditada.isNotEmpty()) {
				tareas[posicion] = tareaEditada
				adapter.notifyDataSetChanged()
				Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show()
			}
			dialog.dismiss()
		}

		builder.setNegativeButton("Cancelar", null)
		builder.show()
	}

	private fun eliminarTarea(posicion: Int) {
		val tarea = tareas[posicion]

		val builder = AlertDialog.Builder(this)
		builder.setTitle("Eliminar")
		builder.setMessage("¿Eliminar '$tarea'?")

		builder.setPositiveButton("Sí") { _, _ ->
			tareas.removeAt(posicion)
			adapter.notifyDataSetChanged()
			Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
		}

		builder.setNegativeButton("No", null)
		builder.show()
	}

	private fun compartirTarea(posicion: Int) {
		val tarea = tareas[posicion]
		Toast.makeText(this, "Compartiendo: $tarea", Toast.LENGTH_SHORT).show()
		// Aquí podrías implementar compartir real con Intent
	}

	// Inflar el menú
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	// Manejar clics en el menú
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_agregar -> {
				mostrarDialogoAgregarTarea()
				true
			}

			R.id.action_eliminar_todas -> {
				eliminarTodasLasTareas()
				true
			}

			R.id.action_configuracion -> {
				Toast.makeText(this, "Configuración (próximamente)", Toast.LENGTH_SHORT).show()
				true
			}

			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun mostrarDialogoAgregarTarea() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Nueva Tarea")

		// Crear campo de texto
		val input = EditText(this)
		input.hint = "Escribe la tarea"
		builder.setView(input)

		// Botón agregar
		builder.setPositiveButton("Agregar") { dialog, _ ->
			val nuevaTarea = input.text.toString()
			if (nuevaTarea.isNotEmpty()) {
				tareas.add(nuevaTarea)
				adapter.notifyDataSetChanged()
				Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show()
			}
			dialog.dismiss()
		}

		// Botón cancelar
		builder.setNegativeButton("Cancelar") { dialog, _ ->
			dialog.cancel()
		}

		builder.show()
	}

	private fun eliminarTodasLasTareas() {
		if (tareas.isEmpty()) {
			Toast.makeText(this, "No hay tareas para eliminar", Toast.LENGTH_SHORT).show()
			return
		}

		val builder = AlertDialog.Builder(this)
		builder.setTitle("Confirmar")
		builder.setMessage("¿Eliminar todas las tareas?")

		builder.setPositiveButton("Sí") { _, _ ->
			tareas.clear()
			adapter.notifyDataSetChanged()
			Toast.makeText(this, "Todas las tareas eliminadas", Toast.LENGTH_SHORT).show()
		}

		builder.setNegativeButton("No", null)
		builder.show()
	}
}