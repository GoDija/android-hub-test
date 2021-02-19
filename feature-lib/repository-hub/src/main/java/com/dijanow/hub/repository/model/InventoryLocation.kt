package com.dijanow.hub.repository.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InventoryLocation(
	val id: String,
	val name: String,
	val code: String,
	val active: Boolean
)