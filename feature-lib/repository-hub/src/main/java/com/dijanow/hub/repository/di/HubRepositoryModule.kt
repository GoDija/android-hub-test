package com.dijanow.hub.repository.di

import com.dijanow.hub.repository.HubRepositoryService
import com.dijanow.hub.repository.MockHubRepositoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [SingletonComponent::class])

object HubRepositoryModule {

	@Provides
	fun providesRetrofit(
	): HubRepositoryService {
		return MockHubRepositoryService
	}


}