package com.cubeai.app

import android.content.Context
import android.graphics.*
import android.view.View

class CubeScannerView(context: Context) : View(context) {
    private val line=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=Color.rgb(48,211,255); style=Paint.Style.STROKE; strokeWidth=5f
    }
    private val shade=Paint().apply { color=Color.argb(105,0,0,0) }

    override fun onDraw(c:Canvas) {
        super.onDraw(c)
        val size=minOf(width,height)*0.68f
        val l=(width-size)/2f; val t=(height-size)/2f
        val r=l+size; val b=t+size
        c.drawRect(0f,0f,width.toFloat(),t,shade)
        c.drawRect(0f,b,width.toFloat(),height.toFloat(),shade)
        c.drawRect(0f,t,l,b,shade); c.drawRect(r,t,width.toFloat(),b,shade)
        c.drawRoundRect(l,t,r,b,24f,24f,line)
        for(i in 1..2) {
            val x=l+size*i/3f; val y=t+size*i/3f
            c.drawLine(x,t,x,b,line); c.drawLine(l,y,r,y,line)
        }
    }
}
