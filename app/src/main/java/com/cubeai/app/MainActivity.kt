package com.cubeai.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView

class MainActivity : Activity() {
    private val bg = Color.rgb(9, 13, 24)
    private val card = Color.rgb(19, 27, 45)
    private val cyan = Color.rgb(56, 217, 255)
    private val purple = Color.rgb(151, 91, 255)
    private val green = Color.rgb(63, 224, 138)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSplash()
    }

    private fun showSplash() {
        val root = vertical(Gravity.CENTER).apply {
            setPadding(dp(28), dp(28), dp(28), dp(28))
            setBackgroundColor(bg)
        }

        val cube = TextView(this).apply {
            text = "🟥 🟦\n🟩 🟨"
            textSize = 38f
            gravity = Gravity.CENTER
        }
        val title = label("CUBE AI", 46f, Color.WHITE, true)
        val tagline = label("Solve • Learn • Improve", 18f, cyan, false)

        root.addView(cube)
        root.addView(space(24))
        root.addView(title)
        root.addView(space(10))
        root.addView(tagline)
        setContentView(root)

        title.startAnimation(AlphaAnimation(0.15f, 1f).apply {
            duration = 900
            repeatMode = Animation.REVERSE
            repeatCount = 1
        })
        cube.startAnimation(ScaleAnimation(
            0.75f, 1f, 0.75f, 1f,
            Animation.RELATIVE_TO_SELF, .5f,
            Animation.RELATIVE_TO_SELF, .5f
        ).apply { duration = 900 })

        Handler(Looper.getMainLooper()).postDelayed({ showHome() }, 1800)
    }

    private fun showHome() {
        val root = vertical(Gravity.TOP).apply {
            setPadding(dp(20), dp(24), dp(20), dp(16))
            setBackgroundColor(bg)
        }

        root.addView(label("CUBE AI", 32f, Color.WHITE, true))
        root.addView(label("Your smart Rubik's Cube coach", 14f, Color.rgb(160, 174, 200), false))
        root.addView(space(22))

        val cubeHero = label("🟥 🟦 🟨\n🟩 ⬜ 🟧", 38f, Color.WHITE, false).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(12))
        }
        root.addView(cubeHero)
        root.addView(space(14))

        val scan = Button(this).apply {
            text = "SCAN & SOLVE"
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = roundedGradient(intArrayOf(cyan, purple), 28f)
            setOnClickListener { showPlaceholder("Scan Your Cube", "Camera scanner is the next build step.") }
        }
        root.addView(scan, LinearLayout.LayoutParams(-1, dp(62)))
        root.addView(space(22))

        val row1 = horizontal()
        row1.addView(featureCard("🤖", "AI Coach", "Learn smarter") { showPlaceholder("AI Coach", "Personal coaching is coming next.") }, weight())
        row1.addView(space(12, horizontal = true))
        row1.addView(featureCard("📘", "Learn", "Step by step") { showPlaceholder("Learning Path", "Beginner → Speed Cuber") }, weight())
        root.addView(row1)

        root.addView(space(12))
        val row2 = horizontal()
        row2.addView(featureCard("⏱", "Practice", "Train & time") { showPlaceholder("Practice", "Cases, timer and custom practice") }, weight())
        row2.addView(space(12, horizontal = true))
        row2.addView(featureCard("📈", "Progress", "Track growth") { showPlaceholder("My Progress", "Solves, best time, streak and history") }, weight())
        root.addView(row2)

        root.addView(Space(this), LinearLayout.LayoutParams(1, 0, 1f))
        root.addView(bottomNav())
        setContentView(root)
    }

    private fun featureCard(icon: String, title: String, sub: String, action: () -> Unit): View {
        return vertical(Gravity.CENTER).apply {
            setPadding(dp(12), dp(16), dp(12), dp(16))
            background = rounded(card, 22f)
            addView(label(icon, 26f, Color.WHITE, false))
            addView(label(title, 17f, Color.WHITE, true))
            addView(label(sub, 12f, Color.rgb(150, 165, 190), false))
            setOnClickListener { action() }
        }
    }

    private fun bottomNav(): View {
        return horizontal().apply {
            gravity = Gravity.CENTER
            background = rounded(Color.rgb(14, 20, 34), 24f)
            setPadding(dp(6), dp(8), dp(6), dp(8))
            listOf("⌂\nHome", "◈\nSolve", "✦\nCoach", "▥\nProgress").forEach { item ->
                addView(label(item, 12f, Color.WHITE, false).apply {
                    gravity = Gravity.CENTER
                    setPadding(dp(4), dp(4), dp(4), dp(4))
                    setOnClickListener {
                        when {
                            item.contains("Home") -> showHome()
                            item.contains("Solve") -> showPlaceholder("Scan Your Cube", "Scan all 6 faces to start solving.")
                            item.contains("Coach") -> showPlaceholder("AI Coach", "Ask, learn and improve.")
                            else -> showPlaceholder("My Progress", "Your solve stats will appear here.")
                        }
                    }
                }, weight())
            }
        }
    }

    private fun showPlaceholder(title: String, message: String) {
        val root = vertical(Gravity.CENTER).apply {
            setPadding(dp(24), dp(24), dp(24), dp(24))
            setBackgroundColor(bg)
        }
        root.addView(label(title, 30f, Color.WHITE, true))
        root.addView(space(14))
        root.addView(label(message, 16f, Color.rgb(170, 185, 210), false).apply { gravity = Gravity.CENTER })
        root.addView(space(28))
        root.addView(Button(this).apply {
            text = "Back to Home"
            setTextColor(Color.WHITE)
            background = rounded(cyan, 24f)
            setOnClickListener { showHome() }
        }, LinearLayout.LayoutParams(-1, dp(54)))
        setContentView(root)
    }

    private fun vertical(gravityValue: Int) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = gravityValue
    }

    private fun horizontal() = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    private fun label(textValue: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        text = textValue
        textSize = size
        setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    private fun space(size: Int, horizontal: Boolean = false) =
        Space(this).apply {
            layoutParams = if (horizontal) LinearLayout.LayoutParams(dp(size), 1)
            else LinearLayout.LayoutParams(1, dp(size))
        }

    private fun weight() = LinearLayout.LayoutParams(0, dp(128), 1f)

    private fun rounded(color: Int, radius: Float) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius.toInt()).toFloat()
    }

    private fun roundedGradient(colors: IntArray, radius: Float) = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT, colors
    ).apply { cornerRadius = dp(radius.toInt()).toFloat() }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showHome()
    }
}
