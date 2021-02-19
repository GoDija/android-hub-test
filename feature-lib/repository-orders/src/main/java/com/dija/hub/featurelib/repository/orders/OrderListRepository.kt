package com.dija.hub.featurelib.repository.orders

import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderItem
import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.dija.hub.featurelib.repository.orders.models.PickerOrdersService
import com.dija.hub.featurelib.repository.orders.models.network.ChangeStatusNetwork
import com.dija.hub.featurelib.repository.orders.models.network.PickerOrderNetwork
import com.dija.hub.featurelib.repository.orders.models.network.toOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderListRepository @Inject constructor(
	private val pickerOrdersService: PickerOrdersService
) {

	val orderFlow = MutableStateFlow(emptyList<Order>())

	private val ordersCache = mutableMapOf<Long, Order>()
	private val productsCache = mutableMapOf<Long, OrderItem>()

	suspend fun changeOrderStatus(orderId: Long, status: OrderStatus) {
		withContext(Dispatchers.IO) {
			pickerOrdersService.changeOrderStatus(orderId, createChangeStatusModel(status = status))

			// refresh local copy
			orderFlow.value.let { oldList ->

				val updated = oldList.toMutableList()
				val index = updated.indexOfFirst { it.id == orderId }
				if(index  != -1){
					updated[index] = updated[index].copy(status = status)
				}

				orderFlow.emit(updated.toList())
			}
		}
	}

	suspend fun refreshNewOrderList(date: String? = null, hubId: String? = null) {

		val queryDate = date ?: kotlin.run {
			val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
			val now = ZonedDateTime.now().minusHours(6)
			now.format(formatter)
		}

		withContext(Dispatchers.IO) {

			val list = kotlin.runCatching { pickerOrdersService.getOrders(queryDate, hubId).map { it.toOrder() }.toMutableList() }
				.onFailure { it.printStackTrace() }
				.getOrThrow()

			orderFlow.emit(
				list.sortedByDescending { it.createdAt }
					.filter { order -> order.status.isNotMoreThan(OrderStatus.PACKED) }
			)

			list.forEach { ordersCache[it.id] = it }
		}

	}

	suspend fun loadFullOrder(orderId: Long): Order {
		return withContext(Dispatchers.IO) {
			val fullOrder = pickerOrdersService.getOrder(orderId).toOrder()

			ordersCache[orderId] = fullOrder
			fullOrder.items.forEach { productsCache[it.id] = it }

			fullOrder
		}
	}

	fun findOrderItem(orderItemId: Long): OrderItem? {
		return productsCache[orderItemId]
	}

	fun findOrder(orderId: Long): Order? {
		return ordersCache[orderId]
	}

	private fun createChangeStatusModel(status: OrderStatus) =
		ChangeStatusNetwork(
			PickerOrderNetwork(
				status = status,
				userName = "Carlo"
			)
		)

}