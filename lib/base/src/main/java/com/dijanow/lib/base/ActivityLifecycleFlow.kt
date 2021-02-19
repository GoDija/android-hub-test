package com.dijanow.lib.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLifecycleFlow @Inject constructor() : Application.ActivityLifecycleCallbacks {

	private val _stateFlow = MutableStateFlow("")
	val stateFlow: StateFlow<String>
		get() = _stateFlow.asStateFlow()


	override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

	}

	override fun onActivityStarted(activity: Activity) {
		_stateFlow.value = activity::class.java.name
	}

	override fun onActivityResumed(activity: Activity) {
	}

	override fun onActivityPaused(activity: Activity) {
	}

	override fun onActivityStopped(activity: Activity) {
	}

	override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
	}

	override fun onActivityDestroyed(activity: Activity) {
	}


}