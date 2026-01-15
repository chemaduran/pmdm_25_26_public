package com.example.toolbartutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

/**
 * DetailFragment - Pantalla de detalle
 * 
 * Fragment que muestra el detalle de un item.
 * Recibe argumentos de forma type-safe con Safe Args.
 */
class DetailFragment : Fragment() {

    // Recibir argumentos de forma type-safe con Safe Args
    // Esto genera código automático para acceder a los argumentos
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usar el argumento recibido de forma segura
        view.findViewById<TextView>(R.id.tvItemId).text = 
            getString(R.string.item_id_format, args.itemId)
    }
}
