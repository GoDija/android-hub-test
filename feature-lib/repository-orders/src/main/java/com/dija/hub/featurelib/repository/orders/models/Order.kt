package com.dija.hub.featurelib.repository.orders.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Order(
	val id: Long,
	val orderDisplayId: String,
	val customer: String = "",
	val createdAt: ZonedDateTime,
	val numberOfItems: Int,
	val storeName: String,
	val deliveryNote: String,
	val fulfilledBy: String,
	val status: OrderStatus,
	val items: List<OrderItem> = emptyList()
) : Parcelable

enum class OrderStatus(private val state: Int) {
	RECEIVED(0),
	PICKLISTED(1),
	ASSIGNED_TO_PICKER(2),
	PICKED(3),
	PACKED(4),
	DISPATCHED(5),
	CANCELLED(6);

	fun isAtLeast(status: OrderStatus): Boolean {
		return this.state >= status.state
	}

	fun isNotMoreThan(status: OrderStatus): Boolean {
		return this.state <= status.state
	}

	fun toHumanReadable(): String {
		return when (this) {
			RECEIVED -> "Received"
			PICKLISTED -> "Picklisted"
			ASSIGNED_TO_PICKER -> "Assigned to Picker"
			PICKED -> "Picked"
			PACKED -> "Packed"
			DISPATCHED -> "Dispatched"
			CANCELLED -> "Cancelled"
		}
	}
}