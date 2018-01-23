package com.santamaria.shakecontrolspotify

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

/**
 * Created by Santamaria on 06/10/2017.
 */
class ShakeDetector : SensorEventListener {

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int, time: Long)
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

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {

                val now = System.currentTimeMillis()
                //Log.d("\n Ema now1:", now.toString())
                //Log.d("Ema mShakeCount1:", mShakeCount.toString())
                // ignore shake events too close to each other (200ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {

                    if (mShakeCount >= 2){
                        //Log.d("Ema reinicio:", mShakeCount.toString())
                        mShakeCount = 0
                    }
                    return
                }

                // reset the shake count after 1.1 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                   // Log.d("Ema mShakeTimestamp2:", mShakeTimestamp.toString())
                    mShakeCount = 0
                    //Log.d("Ema mShakeCount2:", mShakeCount.toString())
                }

                mShakeTimestamp = now
                mShakeCount++

                //Log.d("Ema mShakeCount3:", mShakeCount.toString())

                mListener!!.onShake(mShakeCount, now)
            }
        }
    }

    companion object {
        private val SHAKE_THRESHOLD_GRAVITY = 2f
        private val SHAKE_SLOP_TIME_MS = 200
        private val SHAKE_COUNT_RESET_TIME_MS = 1000
    }
}