package com.dijanow.hub.repository

import android.content.Context
import com.dijanow.hub.repository.model.InventoryLocation
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HubRepository @Inject constructor(
	@ApplicationContext context: Context,
	private val moshi: Moshi,
	private val hubService: HubRepositoryService
) {

	private val preferences = context.getSharedPreferences("hub_preferences", Context.MODE_PRIVATE)
	val selectedHub = MutableSharedFlow<InventoryLocation>(replay = 1)

	init {
		GlobalScope.launch {
			getSelectedInventoryLocation()?.let {
				selectedHub.emit(it)
			}
		}
	}

	suspend fun getSelectedInventoryLocation(): InventoryLocation? {
		return withContext(Dispatchers.IO) {
			val value = preferences.getString(KEY_SELECTED_INVETORY, null) ?: return@withContext null
			moshi.adapter(InventoryLocation::class.java).fromJson(value)
		}
	}

	suspend fun storeSelectedInventoryLocation(value: InventoryLocation) {
		val json = moshi.adapter(InventoryLocation::class.java).toJson(value)
		preferences.edit().putString(KEY_SELECTED_INVETORY, json).apply()
		selectedHub.emit(value)
	}

	suspend fun getAvailableInventoryLocation(): List<InventoryLocation> {
		return withContext(Dispatchers.IO) {
			hubService.getInventoryLocations()
		}
	}

	companion object {
		const val KEY_SELECTED_INVETORY = "selected_inventory"
	}
}