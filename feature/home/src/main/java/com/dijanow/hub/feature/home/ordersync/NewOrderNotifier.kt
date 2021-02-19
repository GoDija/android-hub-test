package com.dijanow.hub.feature.home.ordersync

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.dijanow.hub.feature.home.AlarmPlayer
import com.dijanow.hub.feature.home.HomeActivity
import com.dijanow.hub.feature.home.NotificationProvider
import com.dijanow.hub.feature.home.R
import com.dijanow.lib.base.ActivityLifecycleFlow
import com.dijanow.lib.base.ApplicationLifecycleFlow
import com.dijanow.lib.base.ApplicationLifecycleFlow.ApplicationLifecycle.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewOrderNotifier @Inject constructor(
	@ApplicationContext private val context: Context,
	private val alarmPlayer: AlarmPlayer,
	private val notificationProvider: NotificationProvider,
	private val applicationLifecycleFlow: ApplicationLifecycleFlow,
	private val activityLifecycleFlow: ActivityLifecycleFlow,
) {

	val newOrdersFlow = MutableSharedFlow<List<Order>>(replay = 1)
	private val lastSeenOrders: MutableSet<Long> = mutableSetOf()

	fun onNewOrderListReceived(newList: List<Order>) {
		val newOrders = newList.filter { !lastSeenOrders.contains(it.id) && it.status.isNotMoreThan(OrderStatus.PICKLISTED) }
		lastSeenOrders.addAll(newList.map { it.id })

		if (newOrders.isNotEmpty()) notifyNewOrderReceived(newOrders)
	}

	private fun notifyNewOrderReceived(newOrders: List<Order>) {
		val builder = notificationProvider.buildNotification(
			title = "New Order",
			text = "You got a new order!",
			icon = R.drawable.ic_notification_new_order,
			channel = NotificationProvider.DijaNotificationChannel.NEW_ORDER,
			pendingIntent = PendingIntent.getActivity(context, 3, Intent(context, HomeActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
		)

		builder.setVibrate(longArrayOf(500, 500, 500, 500, 500, 1000))
		builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

		if (canPlayAlarm()) alarmPlayer.playAlarm()

		notificationProvider.showNotification(
			builder,
			NotificationProvider.ReservedNotificationsId.NEW_ORDER
		)

		GlobalScope.launch { newOrdersFlow.emit(newOrders) }
	}

	fun stopOrdersNotification() {
		alarmPlayer.stopAlarm()
		notificationProvider.removeNotification(NotificationProvider.ReservedNotificationsId.NEW_ORDER)
		GlobalScope.launch { newOrdersFlow.emit(emptyList()) }
	}

	private fun canPlayAlarm():Boolean {
		val isInBackground = applicationLifecycleFlow.stateFlow.value == Background
		if (isInBackground) return true

		return activityLifecycleFlow.stateFlow.value.endsWith("HomeActivity")
	}

}