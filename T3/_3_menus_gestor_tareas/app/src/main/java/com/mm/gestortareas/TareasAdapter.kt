package com.mm.gestortareas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast

class TareaAdapter(
	private val context: Context,
	private val tareas: MutableList<String>,
	private val onTareaActualizada: () -> Unit
) : BaseAdapter() {

	override fun getCount(): Int = tareas.size

	override fun getItem(position: Int): String = tareas[position]

	override fun getItemId(position: Int): Long = position.toLong()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: LayoutInflater.from(context)
			.inflate(R.layout.item_tarea, parent, false)

		val tvTarea = view.findViewById<TextView>(R.id.tvTarea)
		val btnOpciones = view.findViewById<ImageButton>(R.id.btnOpciones)

		tvTarea.text = tareas[position]

		btnOpciones.setOnClickListener {
			mostrarMenuPopup(it, position)
		}

		return view
	}

	private fun mostrarMenuPopup(view: View, position: Int) {
		val popup = PopupMenu(context, view)
		popup.menuInflater.inflate(R.menu.menu_popup, popup.menu)

		popup.setOnMenuItemClickListener { menuItem ->
			when (menuItem.itemId) {
				R.id.popup_marcar_importante -> {
					tareas[position] = "⭐ " + tareas[position]
					onTareaActualizada()
					Toast.makeText(context, "Marcada como importante", Toast.LENGTH_SHORT).show()
					true
				}
				R.id.popup_duplicar -> {
					tareas.add(position + 1, tareas[position])
					onTareaActualizada()
					Toast.makeText(context, "Tarea duplicada", Toast.LENGTH_SHORT).show()
					true
				}
				R.id.popup_mover_arriba -> {
					if (position > 0) {
						val tarea = tareas.removeAt(position)
						tareas.add(position - 1, tarea)
						onTareaActualizada()
						Toast.makeText(context, "Tarea movida arriba", Toast.LENGTH_SHORT).show()
					} else {
						Toast.makeText(context, "Ya está en la primera posición", Toast.LENGTH_SHORT).show()
					}
					true
				}
				else -> false
			}
		}

		popup.show()
	}
}