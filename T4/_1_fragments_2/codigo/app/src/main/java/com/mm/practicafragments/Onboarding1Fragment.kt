package com.mm.practicafragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mm.practicafragments.databinding.FragmentOnboarding1Binding
import androidx.navigation.findNavController

/**
 * A simple [Fragment] subclass.
 * Use the [Onboarding1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Onboarding1Fragment : Fragment() {

    private lateinit var botonSiguiente: Button
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        botonSiguiente = view.findViewById(R.id.botonSiguiente)

        botonSiguiente.setOnClickListener {
            navController.navigate(R.id.action_onboarding1Fragment_to_onboarding2Fragment)
        }
    }
}

