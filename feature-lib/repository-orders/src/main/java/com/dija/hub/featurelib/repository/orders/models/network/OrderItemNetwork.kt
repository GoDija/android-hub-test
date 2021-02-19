package com.dija.hub.featurelib.repository.orders.models.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderItemNetwork(
	@Json(name = "id") val id: Long,
	@Json(name = "name") val name: String,
	@Json(name = "quantity") val quantity: Int,
	@Json(name = "barcode") val barcode: String?,
	@Json(name = "shelf_mapping") val shelfMapping: List<String>,
	@Json(name = "image_url") val imageUrl: String?
)