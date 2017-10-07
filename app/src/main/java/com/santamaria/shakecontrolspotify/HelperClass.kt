package com.santamaria.shakecontrolspotify

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Vibrator
import android.view.KeyEvent
import android.widget.Toast
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.MINUTES




/**
 * Created by Santamaria on 07/10/2017.
 */

class HelperClass (private val activity: MainActivity){

    private val SPOTIFY_PACKAGE_NAME = "com.spotify.music"

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

        intent.`package` = SPOTIFY_PACKAGE_NAME
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

        intent.`package` = SPOTIFY_PACKAGE_NAME
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

    fun alertDialogClose() {

        val dialog : android.support.v7.app.AlertDialog = android.support.v7.app.AlertDialog.Builder(activity).create()

        dialog.setTitle("Shake it - Spotify")
        dialog.setMessage("Are you sure you want to close?")
        dialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    activity.finish()
                })
        dialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                DialogInterface.OnClickListener { dialogInterface, i ->
                 //Do nothing
                })

        dialog.show()

    }

}