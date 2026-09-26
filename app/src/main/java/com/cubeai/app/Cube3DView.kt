package com.cubeai.app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

class Cube3DView(context: Context) : View(context) {
    data class V(val x: Double, val y: Double, val z: Double)
    data class Sticker(val p: List<V>, val color: Int, val normal: V)

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.rgb(8, 9, 12)
        strokeWidth = 5f
        strokeJoin = Paint.Join.ROUND
    }
    private var yaw = 28.0
    private var pitch = -24.0

    private val anim = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 10500
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            val t = (it.animatedValue as Float).toDouble()
            yaw = t
            pitch = -22.0 + 13.0 * sin(Math.toRadians(t * 1.25))
            invalidate()
        }
    }

    init { setLayerType(LAYER_TYPE_SOFTWARE, null) }
    override fun onAttachedToWindow() { super.onAttachedToWindow(); if (!anim.isStarted) anim.start() }
    override fun onDetachedFromWindow() { anim.cancel(); super.onDetachedFromWindow() }

    private fun rotate(v: V): V {
        val ry = Math.toRadians(yaw)
        val rx = Math.toRadians(pitch)
        val cy = cos(ry); val sy = sin(ry)
        val cx = cos(rx); val sx = sin(rx)
        val x1 = v.x * cy + v.z * sy
        val z1 = -v.x * sy + v.z * cy
        return V(x1, v.y * cx - z1 * sx, v.y * sx + z1 * cx)
    }

    private fun project(v: V, cx: Float, cy: Float, scale: Float): PointF {
        val camera = 5.2
        val k = camera / (camera - v.z)
        return PointF((cx + v.x * scale * k).toFloat(), (cy + v.y * scale * k).toFloat())
    }

    private fun face(origin: V, u: V, v: V, normal: V, color: Int): List<Sticker> {
        val out = mutableListOf<Sticker>()
        val gap = 0.045
        for (r in 0..2) for (c in 0..2) {
            val a = -1.0 + c * (2.0 / 3.0) + gap
            val b = -1.0 + (c + 1) * (2.0 / 3.0) - gap
            val d = -1.0 + r * (2.0 / 3.0) + gap
            val e = -1.0 + (r + 1) * (2.0 / 3.0) - gap
            fun p(s: Double, t: Double) = V(
                origin.x + u.x*s + v.x*t,
                origin.y + u.y*s + v.y*t,
                origin.z + u.z*s + v.z*t
            )
            out += Sticker(listOf(p(a,d),p(b,d),p(b,e),p(a,e)), color, normal)
        }
        return out
    }

    private fun stickers(): List<Sticker> {
        val red = Color.rgb(220, 35, 45)
        val orange = Color.rgb(255, 105, 0)
        val white = Color.rgb(245, 245, 240)
        val yellow = Color.rgb(255, 210, 0)
        val green = Color.rgb(20, 170, 80)
        val blue = Color.rgb(35, 105, 230)
        return buildList {
            addAll(face(V(0.0,0.0,1.0), V(1.0,0.0,0.0), V(0.0,1.0,0.0), V(0.0,0.0,1.0), red))
            addAll(face(V(0.0,0.0,-1.0), V(-1.0,0.0,0.0), V(0.0,1.0,0.0), V(0.0,0.0,-1.0), orange))
            addAll(face(V(1.0,0.0,0.0), V(0.0,0.0,-1.0), V(0.0,1.0,0.0), V(1.0,0.0,0.0), blue))
            addAll(face(V(-1.0,0.0,0.0), V(0.0,0.0,1.0), V(0.0,1.0,0.0), V(-1.0,0.0,0.0), green))
            addAll(face(V(0.0,-1.0,0.0), V(1.0,0.0,0.0), V(0.0,0.0,1.0), V(0.0,-1.0,0.0), white))
            addAll(face(V(0.0,1.0,0.0), V(1.0,0.0,0.0), V(0.0,0.0,-1.0), V(0.0,1.0,0.0), yellow))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val scale = min(width, height) * 0.34f

        val visible = stickers().map { s ->
            val rn = rotate(s.normal)
            val rp = s.p.map(::rotate)
            Triple(s, rn, rp)
        }.filter { it.second.z > 0.01 }
         .sortedBy { it.third.map { p -> p.z }.average() }

        for ((s, n, rp) in visible) {
            val pts = rp.map { project(it, cx, cy, scale) }
            val path = Path().apply {
                moveTo(pts[0].x, pts[0].y)
                for (i in 1..3) lineTo(pts[i].x, pts[i].y)
                close()
            }
            val light = (0.70 + 0.30 * max(0.0, n.z)).toFloat()
            val base = s.color
            fill.color = Color.rgb(
                (Color.red(base) * light).toInt().coerceIn(0,255),
                (Color.green(base) * light).toInt().coerceIn(0,255),
                (Color.blue(base) * light).toInt().coerceIn(0,255)
            )
            fill.style = Paint.Style.FILL
            fill.setShadowLayer(18f, 0f, 8f, Color.argb(80, 40, 170, 255))
            canvas.drawPath(path, fill)
            fill.clearShadowLayer()
            canvas.drawPath(path, border)

            val shine = Path(path)
            fill.style = Paint.Style.STROKE
            fill.strokeWidth = 1.6f
            fill.color = Color.argb(90, 255, 255, 255)
            canvas.drawPath(shine, fill)
        }
    }
}
