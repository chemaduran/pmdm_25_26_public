package com.mm.animaciones

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener, Animation.AnimationListener {
    private var animacion: Animation? = null
    private var sprites: AnimationDrawable? = null
    private lateinit var imagen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vinculación de escuchadores a componentes
        imagen = findViewById(R.id.imageView)
        imagen.setOnClickListener(this)

        val boton: Button = findViewById(R.id.boton)
        boton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageView -> {
                loadAnimation(view, R.anim.rotacion)
            }
            R.id.boton -> {
                if (sprites != null && sprites!!.isRunning) {
                    sprites!!.stop()
                } else {
                    // 1. Defino y vinculo al componente el drawable de tipo animation_list
                    imagen.setBackgroundResource(R.drawable.sprites)

                    // 2. Obtengo la referencia del conjunto de animation_list
                    sprites = imagen.background as AnimationDrawable

                    // 3. Comienzo de la animación
                    sprites!!.start()
                }
            }
        }
    }

    /* Dado un componente y un recurso de animación, genera y comienza una animación */
    private fun loadAnimation(view: View, res: Int) {
        // 1. Carga de una nueva animación
        animacion = AnimationUtils.loadAnimation(this, res)
        // 2. Vinculación del escuchador de los estados de la animación
        animacion!!.setAnimationListener(this)
        // 3. Comienzo de la animación
        view.startAnimation(animacion)
    }

    override fun onAnimationStart(animation: Animation) {
        Toast.makeText(this, "Empieza la animación", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationEnd(animation: Animation) {
        Toast.makeText(this, "Finalizó la animación", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(animation: Animation) {
    }
}