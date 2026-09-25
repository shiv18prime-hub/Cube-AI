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
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView

class MainActivity : Activity() {
    private val bg = Color.rgb(8, 12, 22)
    private val card = Color.rgb(20, 28, 47)
    private val cyan = Color.rgb(48, 211, 255)
    private val purple = Color.rgb(142, 79, 255)
    private val muted = Color.rgb(158, 173, 201)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSplash()
    }

    private fun showSplash() {
        val root = vertical(Gravity.CENTER).apply {
            setPadding(dp(28), dp(28), dp(28), dp(28))
            setBackgroundColor(bg)
        }
        val cube = cubeGraphic(dp(150))
        val title = gradientTitle("CUBE AI", 46f)
        val tagline = label("Solve • Learn • Improve", 18f, cyan, false).apply { gravity = Gravity.CENTER }

        root.addView(cube)
        root.addView(space(30))
        root.addView(title)
        root.addView(space(12))
        root.addView(tagline)
        setContentView(root)

        cube.startAnimation(android.view.animation.RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, .5f,
            Animation.RELATIVE_TO_SELF, .5f
        ).apply {
            duration = 4200
            repeatCount = Animation.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
        })
        title.startAnimation(AlphaAnimation(.35f, 1f).apply {
            duration = 850
            repeatMode = Animation.REVERSE
            repeatCount = 1
        })
        Handler(Looper.getMainLooper()).postDelayed({ showHome() }, 1900)
    }

    private fun showHome() {
        val page = vertical(Gravity.TOP).apply {
            setPadding(dp(20), dp(18), dp(20), dp(14))
            setBackgroundColor(bg)
        }
        page.addView(gradientTitle("CUBE AI", 32f))
        page.addView(label("Your smart Rubik's Cube coach", 14f, muted, false))
        page.addView(space(14))
        page.addView(cubeGraphic(dp(126)).apply {
            layoutParams = LinearLayout.LayoutParams(-1, dp(126))
            startAnimation(android.view.animation.RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f
            ).apply {
                duration = 5200
                repeatCount = Animation.INFINITE
                interpolator = android.view.animation.LinearInterpolator()
            })
        })
        page.addView(space(14))

        page.addView(Button(this).apply {
            text = "SCAN & SOLVE"
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = gradient(intArrayOf(cyan, purple), 28)
            setOnClickListener { showPlaceholder("Scan Your Cube", "Camera scanner will guide you through all 6 faces.") }
        }, LinearLayout.LayoutParams(-1, dp(62)))

        page.addView(space(18))
        val r1 = horizontal()
        r1.addView(feature("AI", "AI Coach", "Learn smarter") { showPlaceholder("AI Coach", "Your personal cube coach.") }, weight())
        r1.addView(space(12, true))
        r1.addView(feature("01", "Learn", "Step by step") { showPlaceholder("Learning Path", "Beginner → Intermediate → Advanced → Speed Cuber") }, weight())
        page.addView(r1)
        page.addView(space(12))
        val r2 = horizontal()
        r2.addView(feature("⏱", "Practice", "Train & time") { showPlaceholder("Practice", "Cases, timer and custom training.") }, weight())
        r2.addView(space(12, true))
        r2.addView(feature("↗", "Progress", "Track growth") { showPlaceholder("My Progress", "Solves, best time, average and streak.") }, weight())
        page.addView(r2)
        page.addView(space(16))
        page.addView(bottomNav())

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
            addView(page)
        }
        setContentView(scroll)
    }

    private fun cubeGraphic(size: Int): View {
        val outer = vertical(Gravity.CENTER)
        val colors = intArrayOf(
            Color.rgb(239,68,68), Color.rgb(59,130,246), Color.rgb(250,204,21),
            Color.rgb(34,197,94), Color.WHITE, Color.rgb(249,115,22),
            Color.rgb(59,130,246), Color.rgb(239,68,68), Color.rgb(34,197,94)
        )
        var n = 0
        repeat(3) {
            val row = horizontal().apply { gravity = Gravity.CENTER }
            repeat(3) {
                val tile = View(this).apply {
                    background = GradientDrawable().apply {
                        setColor(colors[n++])
                        cornerRadius = dp(5).toFloat()
                        setStroke(dp(2), Color.rgb(10,14,24))
                    }
                }
                row.addView(tile, LinearLayout.LayoutParams(size/4, size/4).apply {
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                })
            }
            outer.addView(row)
        }
        return outer
    }

    private fun feature(mark: String, title: String, sub: String, action: () -> Unit): View =
        vertical(Gravity.CENTER_VERTICAL).apply {
            setPadding(dp(18), dp(14), dp(14), dp(14))
            background = rounded(card, 24)
            addView(label(mark, 18f, cyan, true))
            addView(space(7))
            addView(label(title, 17f, Color.WHITE, true))
            addView(label(sub, 12f, muted, false))
            setOnClickListener { action() }
        }

    private fun bottomNav(): View = horizontal().apply {
        gravity = Gravity.CENTER
        background = rounded(Color.rgb(14,20,34), 24)
        setPadding(dp(4), dp(7), dp(4), dp(7))
        listOf("Home","Solve","Coach","Progress").forEach { name ->
            addView(label(name, 12f, if(name=="Home") cyan else muted, name=="Home").apply {
                gravity = Gravity.CENTER
                setPadding(dp(3), dp(8), dp(3), dp(8))
                setOnClickListener {
                    when(name) {
                        "Home" -> showHome()
                        "Solve" -> showPlaceholder("Scan Your Cube", "Scan all 6 faces to start.")
                        "Coach" -> showPlaceholder("AI Coach", "Ask, learn and improve.")
                        else -> showPlaceholder("My Progress", "Your solve stats will appear here.")
                    }
                }
            }, LinearLayout.LayoutParams(0, dp(46), 1f))
        }
    }

    private fun showPlaceholder(title: String, message: String) {
        val root = vertical(Gravity.CENTER).apply {
            setPadding(dp(24), dp(24), dp(24), dp(24))
            setBackgroundColor(bg)
        }
        root.addView(cubeGraphic(dp(100)))
        root.addView(space(24))
        root.addView(label(title, 30f, Color.WHITE, true).apply { gravity = Gravity.CENTER })
        root.addView(space(12))
        root.addView(label(message, 16f, muted, false).apply { gravity = Gravity.CENTER })
        root.addView(space(28))
        root.addView(Button(this).apply {
            text = "BACK TO HOME"
            setTextColor(Color.WHITE)
            background = gradient(intArrayOf(cyan, purple), 24)
            setOnClickListener { showHome() }
        }, LinearLayout.LayoutParams(-1, dp(54)))
        setContentView(root)
    }

    private fun gradientTitle(value: String, size: Float) = TextView(this).apply {
        text = value
        textSize = size
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER_HORIZONTAL
        setTextColor(Color.WHITE)
        setShadowLayer(18f, 0f, 0f, cyan)
    }

    private fun vertical(g: Int) = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = g }
    private fun horizontal() = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
    private fun label(v:String,s:Float,c:Int,b:Boolean)=TextView(this).apply{
        text=v; textSize=s; setTextColor(c); if(b) typeface=Typeface.DEFAULT_BOLD
    }
    private fun space(v:Int,horizontal:Boolean=false)=Space(this).apply{
        layoutParams=if(horizontal) LinearLayout.LayoutParams(dp(v),1) else LinearLayout.LayoutParams(1,dp(v))
    }
    private fun weight()=LinearLayout.LayoutParams(0,dp(116),1f)
    private fun rounded(c:Int,r:Int)=GradientDrawable().apply{setColor(c);cornerRadius=dp(r).toFloat()}
    private fun gradient(c:IntArray,r:Int)=GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,c).apply{cornerRadius=dp(r).toFloat()}
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { showHome() }
}
