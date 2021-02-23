package com.dija.hub.featurelib.repository.orders.models

import com.dija.hub.featurelib.repository.orders.models.network.ChangeStatusNetwork
import com.dija.hub.featurelib.repository.orders.models.network.OrderItemNetwork
import com.dija.hub.featurelib.repository.orders.models.network.OrderNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

interface PickerOrdersService {

	suspend fun getOrders(
		queryDate: String? = null,
		hubId: String? = null,
	): List<OrderNetwork>

	suspend fun getOrder(
		orderId: Long
	): OrderNetwork

	suspend fun changeOrderStatus(
		orderId: Long,
		changeStatusModel: ChangeStatusNetwork
	)

}

object MockPickerOrdersService : PickerOrdersService {

	private fun generateMockOrder() :OrderNetwork {

		val random = Random(87)
		val orderId = random.nextLong(until = 5000)

		val statuses = OrderStatus.values()
		val status = statuses[random.nextInt(from = 0, until = statuses.size-1)]

		return OrderNetwork(
			orderId,
			"$orderId-${random.nextInt(from = 0, until = 9)}",
			"Will",
			"2020-01-15T10:31:11Z",
			5,
			"Supergood Store",
			"More chocolate please",
			status,

			listOf(
				OrderItemNetwork(
					1,
					"Airpods Pro",
					3,
					"SGWKC6Y3XLKKT",
					listOf("A-01-03"),
					"https://t.infibeam.com/img/othe/0441617/2d/55/airpodsprofloatwirelesschargingcaseopenscreen.jpg.a4c40e2d55.989x590x412.jpg"
				),
			)

		)

	}

	private val mockList = mutableListOf(
		OrderNetwork(
			456,
			"456-2",
			"Boby",
			"2020-01-15T10:31:11Z",
			6,
			"Supergood Store",
			"More chocolate please",
			OrderStatus.PACKED,
			listOf(
				OrderItemNetwork(
					1,
					"Häagen-Dazs Belgian Chocolate 460ml",
					3,
					"3415581113921",
					listOf("P-01-03"),
					""
				),
				OrderItemNetwork(
					2,
					"Orange Juice 2020",
					1,
					"5051559107004",
					listOf("P-04-05"),
					""
				),
			)
		)
	)


	override suspend fun getOrders(queryDate: String?, hubId:String?): List<OrderNetwork> {
		delay(500)
		return mockList.toList()
	}

	override suspend fun getOrder(orderId: Long): OrderNetwork {
		delay(500)
		return mockList.find { it.id == orderId }!!
	}

	override suspend fun changeOrderStatus(orderId: Long, changeStatusModel: ChangeStatusNetwork) {
		delay(500)
		val indexOrder = mockList.indexOfFirst { it.id == orderId }
		mockList[indexOrder] =
			mockList[indexOrder].copy(status = changeStatusModel.pickerOrder.status)
	}

}