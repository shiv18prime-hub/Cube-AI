package com.cubeai.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.*

class Cube3DView(context: Context) : View(context) {
    data class V(val x:Double,val y:Double,val z:Double)
    data class Sticker(var p:List<V>,val color:Int,var normal:V)

    private val fill=Paint(Paint.ANTI_ALIAS_FLAG)
    private val border=Paint(Paint.ANTI_ALIAS_FLAG).apply{style=Paint.Style.STROKE;color=Color.rgb(8,9,12);strokeWidth=3.2f;strokeJoin=Paint.Join.ROUND}
    private val yaw=32.0; private val pitch=-24.0
    private val sequence=arrayOf("R","U","R'","U'","F","L","F'","L'")
    private var moveIndex=0; private var angle=0.0
    private val state=mutableListOf<Sticker>()

    init { setLayerType(LAYER_TYPE_SOFTWARE,null); resetCube() }

    private fun resetCube(){
        state.clear()
        val red=Color.rgb(220,35,45); val orange=Color.rgb(255,105,0)
        val white=Color.rgb(245,245,240); val yellow=Color.rgb(255,210,0)
        val green=Color.rgb(20,170,80); val blue=Color.rgb(35,105,230)
        state+=face(V(0.0,0.0,1.0),V(1.0,0.0,0.0),V(0.0,1.0,0.0),V(0.0,0.0,1.0),red)
        state+=face(V(0.0,0.0,-1.0),V(-1.0,0.0,0.0),V(0.0,1.0,0.0),V(0.0,0.0,-1.0),orange)
        state+=face(V(1.0,0.0,0.0),V(0.0,0.0,-1.0),V(0.0,1.0,0.0),V(1.0,0.0,0.0),blue)
        state+=face(V(-1.0,0.0,0.0),V(0.0,0.0,1.0),V(0.0,1.0,0.0),V(-1.0,0.0,0.0),green)
        state+=face(V(0.0,-1.0,0.0),V(1.0,0.0,0.0),V(0.0,0.0,1.0),V(0.0,-1.0,0.0),white)
        state+=face(V(0.0,1.0,0.0),V(1.0,0.0,0.0),V(0.0,0.0,-1.0),V(0.0,1.0,0.0),yellow)
    }

    private val anim=ValueAnimator.ofFloat(0f,1f).apply{
        duration=850; interpolator=AccelerateDecelerateInterpolator()
        addUpdateListener{ angle=90.0*(it.animatedValue as Float)*dir(sequence[moveIndex]); invalidate() }
        addListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation:Animator){
                commitMove(sequence[moveIndex]); angle=0.0
                moveIndex=(moveIndex+1)%sequence.size
                postDelayed({ if(isAttachedToWindow) start() },220)
            }
        })
    }
    override fun onAttachedToWindow(){super.onAttachedToWindow();postDelayed({if(isAttachedToWindow&&!anim.isRunning)anim.start()},250)}
    override fun onDetachedFromWindow(){anim.cancel();super.onDetachedFromWindow()}

    private fun dir(m:String)=if(m.endsWith("'"))-1.0 else 1.0
    private fun center(s:Sticker)=V(s.p.map{it.x}.average(),s.p.map{it.y}.average(),s.p.map{it.z}.average())
    private fun active(v:V,m:String)=when(m[0]){'R'->v.x>.32;'L'->v.x<-.32;'U'->v.y<-.32;'D'->v.y>.32;'F'->v.z>.32;'B'->v.z<-.32;else->false}

    private fun turn(v:V,m:String,aDeg:Double):V{
        val a=Math.toRadians(aDeg);val c=cos(a);val s=sin(a)
        return when(m[0]){
            'R','L'->V(v.x,v.y*c-v.z*s,v.y*s+v.z*c)
            'U','D'->V(v.x*c+v.z*s,v.y,-v.x*s+v.z*c)
            else->V(v.x*c-v.y*s,v.x*s+v.y*c,v.z)
        }
    }
    private fun commitMove(m:String){
        val a=90.0*dir(m)
        state.filter{active(center(it),m)}.forEach{s->s.p=s.p.map{turn(it,m,a)};s.normal=turn(s.normal,m,a)}
        invalidate()
    }
    private fun viewRotate(v:V):V{
        val ry=Math.toRadians(yaw);val rx=Math.toRadians(pitch)
        val cy=cos(ry);val sy=sin(ry);val cx=cos(rx);val sx=sin(rx)
        val x=v.x*cy+v.z*sy;val z=-v.x*sy+v.z*cy
        return V(x,v.y*cx-z*sx,v.y*sx+z*cx)
    }
    private fun project(v:V,cx:Float,cy:Float,scale:Float):PointF{
        val d=5.2;val k=d/(d-v.z);return PointF((cx+v.x*scale*k).toFloat(),(cy+v.y*scale*k).toFloat())
    }
    private fun face(o:V,u:V,v:V,n:V,color:Int):List<Sticker>{
        val out=mutableListOf<Sticker>();val gap=.045
        for(r in 0..2)for(col in 0..2){
            val a=-1.0+col*2.0/3.0+gap;val b=-1.0+(col+1)*2.0/3.0-gap
            val d=-1.0+r*2.0/3.0+gap;val e=-1.0+(r+1)*2.0/3.0-gap
            fun p(s:Double,t:Double)=V(o.x+u.x*s+v.x*t,o.y+u.y*s+v.y*t,o.z+u.z*s+v.z*t)
            out+=Sticker(listOf(p(a,d),p(b,d),p(b,e),p(a,e)),color,n)
        };return out
    }

    override fun onDraw(canvas:Canvas){
        super.onDraw(canvas);val cx=width/2f;val cy=height/2f;val scale=min(width,height)*.285f
        val core=Paint(Paint.ANTI_ALIAS_FLAG).apply{
            color=Color.rgb(10,13,18);style=Paint.Style.FILL
            setShadowLayer(22f,0f,10f,Color.argb(95,30,170,255))
        }
        val coreSize=scale*2.05f
        canvas.drawRoundRect(cx-coreSize/2,cy-coreSize/2,cx+coreSize/2,cy+coreSize/2,18f,18f,core)
        val m=sequence[moveIndex]
        val visible=state.map{s->
            val act=active(center(s),m)
            val n=viewRotate(if(act)turn(s.normal,m,angle)else s.normal)
            val ps=s.p.map{viewRotate(if(act)turn(it,m,angle)else it)}
            Triple(s,n,ps)
        }.filter{it.second.z>.01}.sortedBy{it.third.map{p->p.z}.average()}
        for((st,n,ps)in visible){
            val q=ps.map{project(it,cx,cy,scale)}
            val path=Path().apply{moveTo(q[0].x,q[0].y);for(i in 1..3)lineTo(q[i].x,q[i].y);close()}
            val light=(.76+.24*max(0.0,n.z)).toFloat();val base=st.color
            fill.style=Paint.Style.FILL;fill.color=Color.rgb((Color.red(base)*light).toInt().coerceIn(0,255),(Color.green(base)*light).toInt().coerceIn(0,255),(Color.blue(base)*light).toInt().coerceIn(0,255))
            fill.setShadowLayer(8f,0f,3f,Color.argb(45,40,170,255));canvas.drawPath(path,fill);fill.clearShadowLayer();canvas.drawPath(path,border)
            fill.style=Paint.Style.STROKE;fill.strokeWidth=1.2f;fill.color=Color.argb(115,255,255,255);canvas.drawPath(path,fill)
        }
    }
}
