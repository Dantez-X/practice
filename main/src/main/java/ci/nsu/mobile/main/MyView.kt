package ci.nsu.mobile.main.ui.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 80f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    var count: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        canvas.drawRect(0f, 0f, width, height, backgroundPaint)
        canvas.drawRect(0f, 0f, width, height, borderPaint)

        canvas.drawText(
            count.toString(),
            width / 2,
            height / 2 + textPaint.textSize / 3,
            textPaint
        )
    }
}