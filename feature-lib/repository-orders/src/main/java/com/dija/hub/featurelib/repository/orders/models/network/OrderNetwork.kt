package com.dija.hub.featurelib.repository.orders.models.network

import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderNetwork(
	@Json(name = "id") val id: Long,
	@Json(name = "order_display_id") val orderDisplayId: String,
	@Json(name = "customer") val customer: String = "",
	@Json(name = "created_at") val createdAt: String,
	@Json(name = "number_of_items") val numberOfItems: Int,
	@Json(name = "store_name") val storeName: String,
	@Json(name = "delivery_note") val deliveryNote: String = "",
	@Json(name = "status") val status: OrderStatus = OrderStatus.PICKLISTED,
	@Json(name = "line_items") val items: List<OrderItemNetwork>? = null,
	@Json(name = "fulfilled_by") val fulfilledBy: String? = null
)