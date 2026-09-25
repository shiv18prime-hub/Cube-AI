package com.cubeai.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.rgb(9, 13, 24))
        }
        root.addView(TextView(this).apply {
            text = "CUBE AI"
            textSize = 42f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Solve • Learn • Improve"
            textSize = 18f
            setTextColor(Color.rgb(150, 220, 255))
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 0)
        })
        setContentView(root)
    }
}
