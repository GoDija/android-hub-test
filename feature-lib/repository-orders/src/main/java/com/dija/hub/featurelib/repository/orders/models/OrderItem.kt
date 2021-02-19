package com.dija.hub.featurelib.repository.orders.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
	val id: Long,
	val name: String,
	val quantity: Int,
	val barcode: String?,
	val shelfMapping: List<String>,
	val imageUrl: String = ""
) : Parcelable