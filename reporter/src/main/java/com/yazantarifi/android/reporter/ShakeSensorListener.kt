package com.yazantarifi.android.reporter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.yazantarifi.android.reporter.screens.ShakeReporterScreen

class ShakeSensorListener constructor(private val context: Context): SensorEventListener {

    private var sensorAccel = 10f
    private var sensorAccelCurrent = SensorManager.GRAVITY_EARTH
    private var sensorAccelLast = SensorManager.GRAVITY_EARTH

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]
            sensorAccelLast = sensorAccelCurrent
            sensorAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = sensorAccelCurrent - sensorAccelLast
            sensorAccel = sensorAccel * 0.9f + delta
            if (sensorAccel > 12) {
                ShakeReporterScreen.startScreen(context)
            }
        }
    }

}
