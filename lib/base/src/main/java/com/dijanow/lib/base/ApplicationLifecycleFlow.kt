package com.dijanow.lib.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationLifecycleFlow @Inject constructor() : DefaultLifecycleObserver {

	private val _stateFlow = MutableStateFlow(ApplicationLifecycle.Background)

	val stateFlow: StateFlow<ApplicationLifecycle>
		get() = _stateFlow.asStateFlow()

	override fun onStart(owner: LifecycleOwner) {
		super.onStart(owner)
		_stateFlow.value = ApplicationLifecycle.Foreground
	}

	override fun onStop(owner: LifecycleOwner) {
		super.onStop(owner)
		_stateFlow.value = ApplicationLifecycle.Background
	}

	enum class ApplicationLifecycle { Foreground, Background }

}