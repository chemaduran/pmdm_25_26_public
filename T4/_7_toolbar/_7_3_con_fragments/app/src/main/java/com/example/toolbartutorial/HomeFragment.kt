package com.example.toolbartutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * HomeFragment - Pantalla de inicio
 * 
 * Fragment principal que muestra las opciones de navegación.
 * El menú de la Toolbar es gestionado globalmente por MainActivity,
 * por lo que todos los Fragments comparten el mismo menú.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navegación a DetailFragment con argumento usando Safe Args
        view.findViewById<Button>(R.id.btnToDetail).setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToDetail(itemId = 42)
            findNavController().navigate(action)
        }

        // Navegación a SettingsFragment
        view.findViewById<Button>(R.id.btnToSettings).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
    }
}
