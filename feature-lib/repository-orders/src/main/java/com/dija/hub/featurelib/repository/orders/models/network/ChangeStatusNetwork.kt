package com.dija.hub.featurelib.repository.orders.models.network

import com.dija.hub.featurelib.repository.orders.models.OrderStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangeStatusNetwork(
	@Json(name = "picker_order") val pickerOrder: PickerOrderNetwork
)

@JsonClass(generateAdapter = true)
class PickerOrderNetwork(
	@Json(name = "status") val status: OrderStatus,
	@Json(name = "user_name") val userName: String
)
