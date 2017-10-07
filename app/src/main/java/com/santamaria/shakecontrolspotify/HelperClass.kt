package com.santamaria.shakecontrolspotify

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Vibrator
import android.view.KeyEvent
import android.widget.Toast


/**
 * Created by Santamaria on 07/10/2017.
 */

class HelperClass (private val activity: MainActivity){

    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    private fun vibrate() {
        var v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(300)
    }

    private fun showMessage(text:String){
        activity.runOnUiThread({ Toast.makeText(activity.applicationContext, "Now Playing " + text, Toast.LENGTH_SHORT).show() })

    }

    fun nextSong(){

        var keyCode = KeyEvent.KEYCODE_MEDIA_NEXT

        var intent = Intent(Intent.ACTION_MEDIA_BUTTON)

        intent.`package` = "com.spotify.music"
        synchronized (this) {
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            activity.applicationContext.sendOrderedBroadcast(intent, null)
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP, keyCode))
            activity.applicationContext.sendOrderedBroadcast(intent, null)

        }

        if (activity.isVibrateOn){
            vibrate()
        }

        if (activity.isShowMessageOn){
            showMessage("Next Song")
        }
    }

    fun previousSong(){

        var keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS

        var intent = Intent(Intent.ACTION_MEDIA_BUTTON)

        intent.`package` = "com.spotify.music"
        synchronized (this) {
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            activity.applicationContext.sendOrderedBroadcast(intent, null)
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_UP, keyCode))
            activity.applicationContext.sendOrderedBroadcast(intent, null)

        }

        if (activity.isVibrateOn){
            vibrate()
        }

        if (activity.isShowMessageOn){
            showMessage("Previous Song")
        }
    }
}