package com.dija.hub.featurelib.repository.orders

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderSyncServiceNotifier @Inject constructor(){

	val orderSyncServiceEnabled = MutableStateFlow(false)

}