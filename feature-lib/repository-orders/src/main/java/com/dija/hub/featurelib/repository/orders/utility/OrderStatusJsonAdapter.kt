package com.dija.hub.featurelib.repository.orders.utility

import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class OrderStatusJsonAdapter : JsonAdapter<OrderStatus>() {

	override fun fromJson(reader: JsonReader): OrderStatus {
		return when (val value = reader.nextString()) {
            "received" -> OrderStatus.RECEIVED
            "picklisted" -> OrderStatus.PICKLISTED
            "assigned_to_pickers" -> OrderStatus.ASSIGNED_TO_PICKER
            "picked" -> OrderStatus.PICKED
            "packed" -> OrderStatus.PACKED
            "dispatched" -> OrderStatus.DISPATCHED
            "cancelled" -> OrderStatus.CANCELLED
			else -> throw IllegalStateException("Cant parse $value as OrderStatus")
		}
	}

	override fun toJson(writer: JsonWriter, value: OrderStatus?) {
		if (value == null) {
			writer.nullValue()
			return
		}

		when (value) {
            OrderStatus.RECEIVED -> writer.value("received")
            OrderStatus.PICKLISTED -> writer.value("picklisted")
            OrderStatus.ASSIGNED_TO_PICKER -> writer.value("assigned_to_pickers")
            OrderStatus.PICKED -> writer.value("picked")
            OrderStatus.PACKED -> writer.value("packed")
            OrderStatus.DISPATCHED -> writer.value("dispatched")
            OrderStatus.CANCELLED -> writer.value("cancelled")
		}

	}


}