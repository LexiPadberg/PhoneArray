package com.wavesciences.phonearray

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveFormView(context: Context? , attrs : AttributeSet?) : View(context,attrs) {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6F
    private var w = 9F

    init{
        paint.color = Color.rgb(244,81,30)
    }
    fun addAmplitude(amp: Float){
        amplitudes.add(amp)

        var left = 0f
        var top = 0f
        var right: Float = left + width
        var bottom = amp

        spikes.add(RectF(left,top,right,bottom))
        invalidate()

    }
    override fun draw(canvas : Canvas?) {
        super.draw(canvas)
        //canvas?.drawRoundRect(RectF(20f,30f, 20f+30f, 30f+60f ), 6f,6f, paint)
        spikes.forEach{
            canvas?.drawRoundRect(it, radius, radius, paint)

        }
    }

}
