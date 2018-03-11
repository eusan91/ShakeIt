package com.santamaria.shakecontrolspotify

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Created by Santamaria on 06/10/2017.
 */
class ShakeDetector : SensorEventListener {

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0
    private val sensibilityArray1 = floatArrayOf(4f, 5f, 6f, 7f, 8f, 9f)
    private val sensibilityArray2 = floatArrayOf(2f, 2.5f, 3f, 4f, 5f, 6f)
    private val SHAKE_SLOP_TIME_MS = 250
    private val SHAKE_COUNT_RESET_TIME_MS = 1000

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {


        if (mListener != null) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val res:Double = (gX * gX + gY * gY + gZ * gZ).toDouble()
            val gForce = Math.sqrt(res)

            val sensibility : FloatArray
            if (MainActivity.gShakeCount == 1){
                sensibility = sensibilityArray1
            } else {
                sensibility = sensibilityArray2
            }

            if (gForce > sensibility[MainActivity.gSensibility]) {

                val now = System.currentTimeMillis()
                //Log.d("ema", "$now")
                // ignore shake events too close to each other (300ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS >= now) {

                    if (mShakeCount >= MainActivity.gShakeCount){
                        mShakeCount = 0
                    }

                    return
                }

                // reset the shake count after 1 seconds of no shakes
                //it will take the last shake time + 1 second
                //if its less than "now" it has been more than 1 second.
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                    mShakeTimestamp = 0L
                }

                mShakeTimestamp = now
                mShakeCount++

                mListener!!.onShake(mShakeCount)

            }
        }
    }
}