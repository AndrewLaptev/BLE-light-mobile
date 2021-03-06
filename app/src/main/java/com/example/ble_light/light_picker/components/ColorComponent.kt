package com.example.ble_light.light_picker.components

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Shader
import android.view.MotionEvent
import android.view.MotionEvent.*
import com.example.ble_light.light_picker.Metrics
import com.example.ble_light.light_picker.Paints
import com.example.ble_light.light_picker.listeners.OnLightComponentSelectionListener

internal abstract class ColorComponent(val metrics: Metrics, val paints: Paints) {
    var id: String = ""

    var radius: Float = 0f

    var fillWidth: Float = 0f
    var strokeWidth: Float = 0f
    var strokeColor: Int = 0

    var indicatorRadius: Float = 0f
    var indicatorStrokeWidth: Float = 0f
    var indicatorStrokeColor: Int = 0

    var indicatorX: Float = 0f
    var indicatorY: Float = 0f

    var angle: Double = 0.0

    private var isTouched = false
    private var colorSelectionListener: OnLightComponentSelectionListener? = null

    abstract fun getShader(): Shader
    abstract fun drawComponent(canvas: Canvas)

    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            ACTION_DOWN -> {
                if (PointF(x, y) in this) {
                    colorSelectionListener?.onLightComponentSelectionStart(metrics.getColor())
                    isTouched = true
                    calculateAngle(x, y)
                    updateComponent(angle)
                    colorSelectionListener?.onLightComponentSelection(metrics.getColor(), angle.toFloat(),
                        metrics.brightness, id)
                }
            }

            ACTION_MOVE -> {
                if (isTouched) {
                    calculateAngle(x, y)
                    updateComponent(angle)
                    colorSelectionListener?.onLightComponentSelection(metrics.getColor(), angle.toFloat(),
                        metrics.brightness, id)
                }
            }

            ACTION_UP -> {
                if (isTouched) colorSelectionListener?.onLightComponentSelectionEnd(metrics.getColor())
                isTouched = false
            }
        }

        return isTouched
    }

    operator fun contains(point: PointF): Boolean {
        val touchRadius = indicatorRadius + indicatorRadius * 0.2
        return point.x in (indicatorX - touchRadius)..(indicatorX + touchRadius) && point.y in (indicatorY - touchRadius)..(indicatorY + touchRadius)
    }

    open fun calculateAngle(x1: Float, y1: Float) {
        val x = x1 - metrics.centerX
        val y = y1 - metrics.centerY
        val c = Math.sqrt((x * x + y * y).toDouble())

        angle = Math.toDegrees(Math.acos(x / c))
        if (y < 0) {
            angle = 360 - angle
        }
    }

    abstract fun updateComponent(angle: Double)

    abstract fun updateAngle(component: Float)

    internal fun setColorSelectionListener(listener: OnLightComponentSelectionListener) {
        colorSelectionListener = listener
    }

    internal fun setRadius(outerRadius: Float, offset: Float) {
        radius = outerRadius - (Math.max(indicatorRadius + indicatorStrokeWidth, fillWidth)) - offset
    }
}