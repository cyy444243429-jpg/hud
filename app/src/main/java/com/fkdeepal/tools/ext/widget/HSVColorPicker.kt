package com.fkdeepal.tools.ext.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class HSVColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentColor = Color.RED
    private var colorChangeListener: ((Int) -> Unit)? = null
    
    // HSV 颜色渐变 (色相环)
    private val hueColors = intArrayOf(
        Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED
    )
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }
    
    private val indicatorFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制颜色渐变条
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            hueColors, null, Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 20f, 20f, paint)
        
        // 绘制当前选择指示器
        val hsvArray = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(currentColor, hsvArray)
        val position = hueToPosition(hsvArray[0])
        val centerY = height / 2f
        
        // 外圈
        canvas.drawCircle(position, centerY, 25f, indicatorFillPaint)
        canvas.drawCircle(position, centerY, 25f, indicatorPaint)
        
        // 内圈显示当前颜色
        indicatorFillPaint.color = currentColor
        canvas.drawCircle(position, centerY, 20f, indicatorFillPaint)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x.coerceIn(0f, width.toFloat())
                currentColor = positionToColor(x)
                colorChangeListener?.invoke(currentColor)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun positionToColor(position: Float): Int {
        val hue = (position / width) * 360f
        val hsv = floatArrayOf(hue, 1f, 1f) // 最大饱和度、亮度
        return Color.HSVToColor(hsv)
    }
    
    private fun hueToPosition(hue: Float): Float {
        return (hue / 360f) * width
    }
    
    fun setOnColorChangeListener(listener: (Int) -> Unit) {
        colorChangeListener = listener
    }
    
    fun setColor(color: Int) {
        currentColor = color
        invalidate()
    }
    
    fun getCurrentColor(): Int {
        return currentColor
    }
}
