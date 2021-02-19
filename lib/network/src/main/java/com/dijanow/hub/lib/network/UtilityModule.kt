package com.dijanow.hub.lib.network

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [SingletonComponent::class])
class UtilityModule {

	@Provides
	fun providesMoshi(): Moshi {
		return Moshi.Builder()
			.build()
	}

}