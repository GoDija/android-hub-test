package com.dija.hub.featurelib.repository.orders.di

import com.dija.hub.featurelib.repository.orders.models.MockPickerOrdersService
import com.dija.hub.featurelib.repository.orders.models.PickerOrdersService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [ServiceComponent::class, SingletonComponent::class])
object PickerNetworkModule {

	@Provides
	fun providesPickerOrdersService(): PickerOrdersService {
		return MockPickerOrdersService
	}

}