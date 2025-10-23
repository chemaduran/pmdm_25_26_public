package com.mm.sharedpreferences

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mm.sharedpreferences.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val EMPTY_VALUE = ""
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        SharedApp.prefs = Prefs(applicationContext)
        setContentView(binding.root)

        configView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSaveValue.setOnClickListener {
            SharedApp.prefs.name = binding.etName.text.toString()
            configView()
        }

        binding.btnDeleteValue.setOnClickListener {
            SharedApp.prefs.name = EMPTY_VALUE
            configView()
        }
    }

    fun showProfile(){
        binding.tvName.visibility = View.VISIBLE
        binding.tvName.text = "Hola ${SharedApp.prefs.name}"
        binding.btnDeleteValue.visibility = View.VISIBLE
        binding.etName.visibility = View.INVISIBLE
        binding.btnSaveValue.visibility = View.INVISIBLE
    }

    fun showGuest(){
        binding.tvName.visibility = View.INVISIBLE
        binding.btnDeleteValue.visibility = View.INVISIBLE
        binding.etName.visibility = View.VISIBLE
        binding.btnSaveValue.visibility = View.VISIBLE
    }

    fun configView(){
        if(isSavedName()) showProfile()
        else showGuest()
    }

    fun isSavedName():Boolean{
        val myName = SharedApp.prefs.name
        return myName != EMPTY_VALUE
    }
}