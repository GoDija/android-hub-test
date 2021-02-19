package com.dijanow.hub

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.dijanow.lib.base.ActivityLifecycleFlow
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.dijanow.lib.base.ApplicationLifecycleFlow

@HiltAndroidApp
class HubApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var hiltWorkerFactory: HiltWorkerFactory

	@Inject
	lateinit var applicationLifecycleFlow: ApplicationLifecycleFlow

	@Inject
	lateinit var activityLifecycleFlow: ActivityLifecycleFlow

	override fun onCreate() {
		super.onCreate()

		ProcessLifecycleOwner.get()
			.lifecycle.addObserver(applicationLifecycleFlow)

		registerActivityLifecycleCallbacks(activityLifecycleFlow)
	}

	override fun getWorkManagerConfiguration(): Configuration {
		return Configuration.Builder()
			.setWorkerFactory(hiltWorkerFactory)
			.build()
	}

}