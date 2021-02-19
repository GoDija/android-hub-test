package com.dija.hub.featurelib.repository.orders.models.network

import com.dija.hub.featurelib.repository.orders.models.Order
import com.dija.hub.featurelib.repository.orders.models.OrderItem
import java.time.ZonedDateTime

fun OrderNetwork.toOrder(): Order {
	return Order(
		id = id,
		orderDisplayId = orderDisplayId,
		customer = customer,
		createdAt = ZonedDateTime.parse(createdAt),
		numberOfItems = numberOfItems,
		storeName = storeName,
		deliveryNote = deliveryNote,
		status = status,
		items = items?.map { it.toOrderItem() } ?: emptyList(),
		fulfilledBy = fulfilledBy ?: "Dija"
	)
}

fun OrderItemNetwork.toOrderItem(): OrderItem {
	return OrderItem(
		id = id,
		name = name,
		quantity = quantity,
		barcode = barcode,
		shelfMapping = shelfMapping,
		imageUrl = imageUrl ?: ""
	)
}