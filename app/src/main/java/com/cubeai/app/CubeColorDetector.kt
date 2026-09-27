package com.cubeai.app

import android.graphics.Color
import androidx.camera.core.ImageProxy
import kotlin.math.pow

object CubeColorDetector {
    data class Sample(val label: Char, val color: Int)
    private val refs = listOf(
        'W' to intArrayOf(235,235,225), 'Y' to intArrayOf(245,205,20),
        'R' to intArrayOf(205,35,40), 'O' to intArrayOf(245,105,15),
        'G' to intArrayOf(25,155,75), 'B' to intArrayOf(35,95,205)
    )

    fun detect(image: ImageProxy): List<Sample> {
        val plane=image.planes[0]; val buf=plane.buffer
        val w=image.width; val h=image.height
        val row=plane.rowStride; val pix=plane.pixelStride
        val out=ArrayList<Sample>(9)
        for(r in 0..2) for(c in 0..2) {
            val x=(w*(0.32f+c*0.18f)).toInt().coerceIn(0,w-1)
            val y=(h*(0.32f+r*0.18f)).toInt().coerceIn(0,h-1)
            var rr=0;var gg=0;var bb=0;var n=0
            for(dy in -4..4 step 2) for(dx in -4..4 step 2) {
                val xx=(x+dx).coerceIn(0,w-1);val yy=(y+dy).coerceIn(0,h-1)
                val pos=yy*row+xx*pix
                if(pos+2<buf.limit()){bb+=buf.get(pos).toInt() and 255;gg+=buf.get(pos+1).toInt() and 255;rr+=buf.get(pos+2).toInt() and 255;n++}
            }
            rr/=n.coerceAtLeast(1);gg/=n.coerceAtLeast(1);bb/=n.coerceAtLeast(1)
            val lab=refs.minBy{(_,v)->(rr-v[0]).toDouble().pow(2)+(gg-v[1]).toDouble().pow(2)+(bb-v[2]).toDouble().pow(2)}.first
            out+=Sample(lab,Color.rgb(rr,gg,bb))
        }
        return out
    }
}
