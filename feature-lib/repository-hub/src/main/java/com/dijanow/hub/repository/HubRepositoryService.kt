package com.dijanow.hub.repository

import com.dijanow.hub.repository.model.InventoryLocation
import kotlinx.coroutines.delay
import retrofit2.http.GET

interface HubRepositoryService {

	suspend fun getInventoryLocations():List<InventoryLocation>

}

object MockHubRepositoryService: HubRepositoryService {

	val listHubs = listOf(
		InventoryLocation(
			id = "abc-123",
			name = "London - North Hub",
			code = "LNH",
			active = true
		),

		InventoryLocation(
			id = "asd-456",
			name = "London - West Hub",
			code = "LWH",
			active = true
		),

		InventoryLocation(
			id = "xxx-999",
			name = "London - Central Hub",
			code = "LCH",
			active = false
		),
	)

	override suspend fun getInventoryLocations(): List<InventoryLocation> {
		delay(500)

		return listHubs
	}

}