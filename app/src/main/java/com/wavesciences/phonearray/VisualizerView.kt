package com.wavesciences.phonearray
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private var waveform: ByteArray? = null

    fun updateVisualizer(waveform: ByteArray?) {
        this.waveform = waveform
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        waveform?.let {
            val width = width
            val height = height
            val centerY = height / 2f
            val maxValue = Byte.MAX_VALUE.toFloat()
            val sampleSize = it.size / width
            val xScale = width.toFloat() / it.size

            paint.color = Color.GREEN
            paint.strokeWidth = 2f

            for (i in 0 until width) {
                val startIndex = i * sampleSize
                val endIndex = (i + 1) * sampleSize
                var sum = 0f

                for (j in startIndex until endIndex) {
                    sum += it[j].toFloat() / maxValue
                }

                val average = sum / sampleSize
                val scaledAverage = centerY - average * centerY
                canvas.drawLine(
                    i.toFloat(), centerY,
                    i.toFloat(), scaledAverage,
                    paint
                )
            }
        }
    }
}
