package com.mm.t2_layouts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // LINEAR LAYOUT
//         setContentView(R.layout.linear_layout_horizontal)
//         setContentView(R.layout.linear_layout_vertical_1)
//         setContentView(R.layout.linear_layout_vertical_2)
//         setContentView(R.layout.linear_layout_vertical_3)
//         setContentView(R.layout.linear_layout_vertical_4)
//         setContentView(R.layout.linear_layout_vertical_5)

//         TABLE LAYOUT
//         setContentView(R.layout.table_layout_1)
//         setContentView(R.layout.table_layout_2)

//         SCROLLVIEW LAYOUT
//         setContentView(R.layout.scrollview_layout_1)
//         setContentView(R.layout.scrollview_layout_2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}