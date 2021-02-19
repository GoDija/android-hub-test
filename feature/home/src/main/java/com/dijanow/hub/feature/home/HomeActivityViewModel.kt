package com.dijanow.hub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dijanow.hub.feature.home.ordersync.NewOrderNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeActivityViewModel @Inject constructor(
	private val newOrderNotifier: NewOrderNotifier
) : ViewModel() {

	val viewModelFlow = MutableStateFlow(ViewModelView())

	init {

		viewModelScope.launch {
			newOrderNotifier.newOrdersFlow.collect {
				if (it.isNotEmpty()) {
					viewModelFlow.emit(viewModelFlow.value.copy(newOrderDialog = true))
				}
			}
		}

	}

	suspend fun newOrderDialogSeen() {
		viewModelFlow.emit(viewModelFlow.value.copy(newOrderDialog = false))
		newOrderNotifier.stopOrdersNotification()
	}

	data class ViewModelView(
		val newOrderDialog: Boolean = false,
	)
}