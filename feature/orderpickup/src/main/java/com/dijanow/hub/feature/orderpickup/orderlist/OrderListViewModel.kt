package com.dijanow.hub.feature.orderpickup.orderlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dija.hub.featurelib.repository.orders.OrderListRepository
import com.dija.hub.featurelib.repository.orders.OrderSyncServiceNotifier
import com.dija.hub.featurelib.repository.orders.models.Order
import com.dijanow.hub.repository.HubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
	private val orderListRepository: OrderListRepository,
	private val hubRepository: HubRepository,
	private val orderSyncServiceNotifier: OrderSyncServiceNotifier
) : ViewModel() {
	val orderListViewFlow = MutableStateFlow(
		OrderListView(
			items = emptyList(),
			hubName = "No Hub Selected",
			syncEnabled = false,
			allowEnableSync = false
		)
	)

	init {
		viewModelScope.launch {

			combine(
				orderListRepository.orderFlow,
				hubRepository.selectedHub,
				orderSyncServiceNotifier.orderSyncServiceEnabled,
			) { items, hub, orderSyncEnabled ->
				OrderListView(
					items = items,
					hubName = hub.name,
					syncEnabled = orderSyncEnabled,
					allowEnableSync = true
				)
			}.collect {
				orderListViewFlow.emit(it)
			}
		}

	}

	fun onOrderClicked(order: Order): Flow<OrderClickedUseCase> {

		return flow {
			emit(OrderClickedUseCase(loading = true))

			val loadedOrder = kotlin.runCatching { orderListRepository.loadFullOrder(orderId = order.id) }
				.onFailure { it.printStackTrace() }
				.getOrThrow()

			emit(OrderClickedUseCase(loading = false, order = loadedOrder))
		}

	}

	data class OrderClickedUseCase(
		val loading: Boolean,
		val order: Order? = null
	)

	data class OrderListView(
		val items: List<Order>,
		val hubName: String,
		val syncEnabled: Boolean,
		val allowEnableSync: Boolean
	)

}