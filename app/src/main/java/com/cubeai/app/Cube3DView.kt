package com.cubeai.app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

class Cube3DView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(7,10,16); style = Paint.Style.STROKE; strokeWidth = 5f
    }
    private var ax = -22.0
    private var ay = 28.0
    private val animator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 9000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            val t = (it.animatedValue as Float).toDouble()
            ay = t
            ax = -20.0 + 10.0 * sin(Math.toRadians(t * 1.35))
            invalidate()
        }
    }

    init { setLayerType(LAYER_TYPE_SOFTWARE, null) }
    override fun onAttachedToWindow() { super.onAttachedToWindow(); animator.start() }
    override fun onDetachedFromWindow() { animator.cancel(); super.onDetachedFromWindow() }

    data class P3(val x:Double,val y:Double,val z:Double)
    data class Face(val pts:List<P3>,val colors:IntArray,val normal:P3)

    private fun rot(p:P3):P3 {
        val xR=Math.toRadians(ax); val yR=Math.toRadians(ay)
        val cy=cos(yR); val sy=sin(yR); val cx=cos(xR); val sx=sin(xR)
        val x=p.x*cy+p.z*sy
        val z1=-p.x*sy+p.z*cy
        val y=p.y*cx-z1*sx
        val z=p.y*sx+z1*cx
        return P3(x,y,z)
    }
    private fun project(p:P3,cx:Float,cy:Float,s:Float):PointF {
        val d=4.4; val k=d/(d-p.z)
        return PointF((cx+p.x*s*k).toFloat(),(cy+p.y*s*k).toFloat())
    }

    override fun onDraw(c:Canvas) {
        super.onDraw(c)
        val s=min(width,height)*0.31f
        val cx=width/2f; val cy=height/2f
        paint.setShadowLayer(26f,0f,12f,Color.argb(110,30,170,255))

        val faces=listOf(
            Face(listOf(P3(-1,-1,1),P3(1,-1,1),P3(1,1,1),P3(-1,1,1)),
                intArrayOf(0xFFF44336.toInt(),0xFF1976F3.toInt(),0xFFFFC107.toInt(),0xFF22C55E.toInt(),0xFFFFFFFF.toInt(),0xFFFF6D00.toInt(),0xFF1976F3.toInt(),0xFFF44336.toInt(),0xFF22C55E.toInt()),P3(0.0,0.0,1.0)),
            Face(listOf(P3(1,-1,1),P3(1,-1,-1),P3(1,1,-1),P3(1,1,1)),
                intArrayOf(0xFFFF6D00.toInt(),0xFF22C55E.toInt(),0xFFF44336.toInt(),0xFF1976F3.toInt(),0xFFFFC107.toInt(),0xFFFFFFFF.toInt(),0xFF22C55E.toInt(),0xFFFF6D00.toInt(),0xFF1976F3.toInt()),P3(1.0,0.0,0.0)),
            Face(listOf(P3(-1,-1,-1),P3(1,-1,-1),P3(1,-1,1),P3(-1,-1,1)),
                intArrayOf(0xFFFFFFFF.toInt(),0xFFFFC107.toInt(),0xFF1976F3.toInt(),0xFFF44336.toInt(),0xFF22C55E.toInt(),0xFFFF6D00.toInt(),0xFFFFC107.toInt(),0xFF1976F3.toInt(),0xFFFFFFFF.toInt()),P3(0.0,-1.0,0.0))
        )
        val visible=faces.map { f -> f to rot(f.normal) }.filter { it.second.z>0.02 }.sortedBy { it.second.z }
        for((face,_) in visible) drawFace(c,face,cx,cy,s)
        paint.clearShadowLayer()
    }

    private fun drawFace(c:Canvas,f:Face,cx:Float,cy:Float,s:Float) {
        val a=rot(f.pts[0]); val b=rot(f.pts[1]); val d=rot(f.pts[3])
        for(r in 0..2) for(col in 0..2) {
            fun mix(u:Double,v:Double)=P3(
                a.x+(b.x-a.x)*u+(d.x-a.x)*v,
                a.y+(b.y-a.y)*u+(d.y-a.y)*v,
                a.z+(b.z-a.z)*u+(d.z-a.z)*v)
            val gap=.035
            val u0=col/3.0+gap; val u1=(col+1)/3.0-gap
            val v0=r/3.0+gap; val v1=(r+1)/3.0-gap
            val pts=listOf(mix(u0,v0),mix(u1,v0),mix(u1,v1),mix(u0,v1)).map{project(it,cx,cy,s)}
            val path=Path().apply{
                moveTo(pts[0].x,pts[0].y); lineTo(pts[1].x,pts[1].y); lineTo(pts[2].x,pts[2].y); lineTo(pts[3].x,pts[3].y); close()
            }
            paint.color=f.colors[r*3+col]
            paint.style=Paint.Style.FILL
            c.drawPath(path,paint)
            c.drawPath(path,edge)
        }
    }
}
