package com.dijanow.hub.feature.home.ordersync

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.dija.hub.featurelib.repository.orders.OrderListRepository
import com.dija.hub.featurelib.repository.orders.OrderSyncServiceNotifier
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.dijanow.hub.feature.home.HomeActivity
import com.dijanow.hub.feature.home.NotificationProvider
import com.dijanow.hub.feature.home.NotificationProvider.ReservedNotificationsId
import com.dijanow.hub.feature.home.R
import com.dijanow.hub.repository.HubRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TIME_TO_REFRESH: Long = 10 * 1000

@AndroidEntryPoint
class OrderSyncService : Service() {

	@Inject
	lateinit var notificationProvider: NotificationProvider

	@Inject
	lateinit var orderRepository: OrderListRepository
	@Inject
	lateinit var hubRepository: HubRepository

	@Inject
	lateinit var newOrderNotifier: NewOrderNotifier

	@Inject
	lateinit var orderSyncServiceNotifier: OrderSyncServiceNotifier

	lateinit var serviceScope: CoroutineScope

	override fun onCreate() {
		super.onCreate()
		serviceScope = MainScope()

		val notification = notificationProvider.buildNotification(
            title = "Order Sync",
            text = "Syncing new Dija orders",
            icon = R.drawable.ic_notification_d,
            channel = NotificationProvider.DijaNotificationChannel.NEW_ORDER,
            pendingIntent = PendingIntent.getActivity(this, 3, Intent(this, HomeActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        )

		startForeground(ReservedNotificationsId.SYNC_ORDER, notification.build())

		startSync()

		serviceScope.launch {
			orderSyncServiceNotifier.orderSyncServiceEnabled.emit(true)
		}
	}

	private fun startSync() {

		serviceScope.launch(Dispatchers.IO) {

			while (isActive) {

				kotlin.runCatching {
					val selectedHubId = hubRepository.getSelectedInventoryLocation()?.id
					orderRepository.refreshNewOrderList(hubId = selectedHubId)
				}
					.onFailure { it.printStackTrace() }

				delay(TIME_TO_REFRESH)
			}

		}

		serviceScope.launch {
			orderRepository.orderFlow.collect { newList -> newOrderNotifier.onNewOrderListReceived(newList)}
		}

	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onDestroy() {
		orderSyncServiceNotifier.orderSyncServiceEnabled.tryEmit(false)
		serviceScope.cancel()
		stopForeground(true)
		newOrderNotifier.stopOrdersNotification()

		super.onDestroy()
	}
}