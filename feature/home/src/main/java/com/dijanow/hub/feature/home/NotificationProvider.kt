package com.dijanow.hub.feature.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationProvider @Inject constructor(@ApplicationContext private val context: Context) {

	init {
		DijaNotificationChannel.values().forEach {
			createNotificationChannel(it)
		}
	}

	fun buildNotification(
        title: String,
        text: String,
        @DrawableRes icon: Int = R.drawable.ic_notification_d,
        channel: DijaNotificationChannel,
        pendingIntent: PendingIntent? = null
    ): NotificationCompat.Builder {
		val builder = NotificationCompat.Builder(context, channel.channelId)
			.setSmallIcon(icon)
			.setContentTitle(title)
			.setContentText(text)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)

		if (pendingIntent != null) {
			builder.setContentIntent(pendingIntent)
			builder.setAutoCancel(true)
		}

		return builder
	}

	fun showNotification(
        title: String,
        text: String,
        @DrawableRes icon: Int = R.drawable.ic_notification_d,
        channel: DijaNotificationChannel,
        notificationId: Int,
        pendingIntent: PendingIntent? = null
    ) {
		val builder = buildNotification(title, text, icon, channel, pendingIntent)
		showNotification(builder, notificationId)
	}

	fun showNotification(builder: NotificationCompat.Builder, notificationId: Int) {
		with(NotificationManagerCompat.from(context)) {
			// notificationId is a unique int for each notification that you must define
			notify(notificationId, builder.build())
		}
	}

	fun removeNotification(notificationId: Int) {
		with(NotificationManagerCompat.from(context)) {
			cancel(notificationId)
		}
	}

	private fun createNotificationChannel(channel: DijaNotificationChannel) {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = channel.channelName
			val descriptionText = channel.channelDescrition
			val importance = channel.importance
			val notificationChannel = NotificationChannel(channel.channelId, name, importance)
				.also { it.description = descriptionText }
			// Register the channel with the system
			val notificationManager: NotificationManager =
				context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(notificationChannel)
		}
	}


	enum class DijaNotificationChannel(
        val channelName: String,
        val channelDescrition: String,
        val channelId: String,
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
		NEW_ORDER("New Order", "Showing new orders", "new_order"),
	}

	object ReservedNotificationsId {
		const val NEW_ORDER = 1
		const val SYNC_ORDER = 2
	}
}