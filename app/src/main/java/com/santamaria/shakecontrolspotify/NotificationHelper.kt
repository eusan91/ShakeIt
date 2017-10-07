package com.santamaria.shakecontrolspotify

import android.content.Context
import android.support.v7.app.NotificationCompat
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent

/**
 * Created by Santamaria on 07/10/2017.
 */

class NotificationHelper(private var context:Context){

    // Sets an ID for the notification
    private val mNotificationId = 21

    // Gets an instance of the NotificationManager service
    private val mNotifyMgr = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(){

        val resultIntent = Intent(context, MainActivity::class.java)

        val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Shake it - Spotify")
                .setContentText(context.getString(R.string.msg_notification))
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    fun cancelNotification(){

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(mNotificationId)
    }


}