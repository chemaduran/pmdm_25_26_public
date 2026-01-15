package com.mm.practicafragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController


/**
 * A simple [Fragment] subclass.
 * Use the [Onboarding2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Onboarding2Fragment : Fragment() {

    private lateinit var botonFinalizar: Button
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        botonFinalizar = view.findViewById(R.id.botonFinalizar)

        botonFinalizar.setOnClickListener {
            navController.navigate(R.id.action_onboarding2Fragment_to_homeFragment)
        }
    }
}