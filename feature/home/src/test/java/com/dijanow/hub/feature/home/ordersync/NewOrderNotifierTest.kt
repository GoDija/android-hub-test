package com.dijanow.hub.feature.home.ordersync

import android.content.Context
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderItem
import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.dijanow.hub.feature.home.AlarmPlayer
import com.dijanow.hub.feature.home.NotificationProvider
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class NewOrderNotifierTest {

	lateinit var newOrderNotifier: NewOrderNotifier

	val context: Context = mockk(relaxed = true)
	val alarmPlayer: AlarmPlayer = mockk(relaxed = true)
	val notificationProvider: NotificationProvider = mockk(relaxed = true)

	@Before
	fun setUp() {
		newOrderNotifier = NewOrderNotifier(context, alarmPlayer, notificationProvider)
	}

	@Test
	fun `test new order notifies`() {
		newOrderNotifier.onNewOrderListReceived(createOrderList())

		verify { alarmPlayer.playAlarm() }
	}

	@Test
	fun `2 different orders notifies twice`() {
		val first = createOrderList()
		newOrderNotifier.onNewOrderListReceived(first)
		newOrderNotifier.onNewOrderListReceived(first + createOrderList(id = 4))

		verify(exactly = 2) { alarmPlayer.playAlarm() }
	}

	@Test
	fun `same orders list plays only once`() {
		val first = createOrderList()
		newOrderNotifier.onNewOrderListReceived(first)
		newOrderNotifier.onNewOrderListReceived(first)

		verify(exactly = 1) { alarmPlayer.playAlarm() }
	}

	@Test
	fun `2 different orders but one already picked notifies twice`() {
		val first = createOrderList()
		newOrderNotifier.onNewOrderListReceived(first)
		newOrderNotifier.onNewOrderListReceived(first + createOrderList(id = 4, status = OrderStatus.ASSIGNED_TO_PICKER))

		verify(exactly = 1) { alarmPlayer.playAlarm() }
	}


	private fun createOrderList(id: Long = 1, status: OrderStatus = OrderStatus.PICKLISTED): MutableList<Order> {

		return mutableListOf(
			Order(
				id = id,
				orderDisplayId = "id_$id",
				customer = "Fake Cust $id",
				createdAt = ZonedDateTime.now(),
				numberOfItems = 4,
				storeName = "store name",
				deliveryNote = "notes",
				status = status,
				items = listOf(
					OrderItem(
						id = 4 + id,
						name = "Coke",
						quantity = 2,
						barcode = "23423423",
						shelfMapping = emptyList(),
						imageUrl = "https://sdfsdfsfdd.com"
					)
				)
			)
		)

	}


}