package com.aemerse.muserse.notification

import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aemerse.muserse.model.Song
import com.aemerse.muserse.R

class CreateNotification {
    companion object {
        fun notification(context: Context, track: Song, playButton: Int, pause: Int, size: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
                val mediaSessionCompat: MediaSessionCompat = MediaSessionCompat(context, "tag")

                val CHANNEL_ID = "Your_Channel_ID"
                var ACTION_PREVIUOS = "Action_Previous"
                var CHANNEL_PLAY = "Action_Play"
                var CHANNEL_NEXT = "Action_Next"

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(track.title)
                    .setContentText(track.desc)
                    .setSmallIcon(R.drawable.ic_baseline_arrow_back_24)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
                notificationManagerCompat.notify(1, notification)

            }
        }
    }
}