package com.santamaria.shakecontrolspotify

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Vibrator
import android.view.KeyEvent
import android.widget.Toast


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
        activity.runOnUiThread({ Toast.makeText(activity.applicationContext, activity.getString(R.string.now_playing_toast) + " " + text, Toast.LENGTH_SHORT).show() })

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
            showMessage(activity.getString(R.string.next_song_toast))
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
            showMessage(activity.getString(R.string.previous_song_toast))
        }
    }

    fun alertDialogClose() {

        val dialog : android.support.v7.app.AlertDialog = android.support.v7.app.AlertDialog.Builder(activity).create()

        dialog.setTitle("Shake it - Spotify")
        dialog.setMessage(activity.getString(R.string.exit_message))
        dialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                DialogInterface.OnClickListener { _, _ ->
                    activity.finish()
                })
        dialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.CANCEL_BUTTON),
                DialogInterface.OnClickListener { _, _ ->
                 //Do nothing
                })

        dialog.show()

    }

}