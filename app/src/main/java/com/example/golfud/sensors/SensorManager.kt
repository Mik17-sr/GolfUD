package com.example.golfud.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.golfud.data.Vector2D
import kotlin.math.sqrt

class SwingDetector(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gravity = FloatArray(3)
    private var gravityInitialized = false

    private var isSwinging = false
    private var lockedDirection = Vector2D(0f, -1f)
    private var peakMagnitude = 0f

    var onSwingDetected: ((Vector2D) -> Unit)? = null
    var enabled: Boolean = true

    var aimDirection by mutableStateOf(Vector2D(0f, -1f))
        private set
    var previewVector by mutableStateOf<Vector2D?>(Vector2D(0f, -18f))
        private set
    var debugMagnitude by mutableStateOf(0f)
        private set

    companion object {
        private const val GRAVITY_ALPHA = 0.85f
        private const val TILT_DEADZONE = 1.0f
        private const val SWING_START_THRESHOLD = 3.5f
        private const val SWING_END_THRESHOLD = 1.2f
        private const val POWER_SCALE = 8f
        private const val MIN_POWER = 5f
        private const val MAX_POWER = 60f
        private const val AIM_PREVIEW_LENGTH = 18f
    }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!enabled) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        if (!gravityInitialized) {
            gravity[0] = event.values[0]
            gravity[1] = event.values[1]
            gravity[2] = event.values[2]
            gravityInitialized = true
        } else {
            gravity[0] = GRAVITY_ALPHA * gravity[0] + (1 - GRAVITY_ALPHA) * event.values[0]
            gravity[1] = GRAVITY_ALPHA * gravity[1] + (1 - GRAVITY_ALPHA) * event.values[1]
            gravity[2] = GRAVITY_ALPHA * gravity[2] + (1 - GRAVITY_ALPHA) * event.values[2]
        }

        val tiltMagnitude = sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1])
        if (tiltMagnitude > TILT_DEADZONE) {
            aimDirection = Vector2D(gravity[0] / tiltMagnitude, -gravity[1] / tiltMagnitude)
        }

        val linearX = event.values[0] - gravity[0]
        val linearY = event.values[1] - gravity[1]
        val magnitude = sqrt(linearX * linearX + linearY * linearY)
        debugMagnitude = magnitude

        if (!isSwinging) {
            previewVector = Vector2D(aimDirection.x * AIM_PREVIEW_LENGTH, aimDirection.y * AIM_PREVIEW_LENGTH)
        }

        if (!isSwinging && magnitude > SWING_START_THRESHOLD) {
            isSwinging = true
            lockedDirection = aimDirection
            peakMagnitude = magnitude
        }

        if (isSwinging) {
            if (magnitude > peakMagnitude) peakMagnitude = magnitude
            val power = (peakMagnitude * POWER_SCALE).coerceIn(MIN_POWER, MAX_POWER)
            previewVector = Vector2D(lockedDirection.x * power, lockedDirection.y * power)

            if (magnitude < SWING_END_THRESHOLD) {
                isSwinging = false
                val finalPower = (peakMagnitude * POWER_SCALE).coerceIn(MIN_POWER, MAX_POWER)
                onSwingDetected?.invoke(Vector2D(lockedDirection.x * finalPower, lockedDirection.y * finalPower))
                previewVector = Vector2D(aimDirection.x * AIM_PREVIEW_LENGTH, aimDirection.y * AIM_PREVIEW_LENGTH)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}