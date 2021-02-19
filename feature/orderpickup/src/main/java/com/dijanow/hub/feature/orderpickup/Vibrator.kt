package com.dijanow.hub.feature.orderpickup

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class Vibrator @Inject constructor(@ApplicationContext private val context: Context) {

	val vibratorService by lazy { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

	fun shakeIt(millis: Long = 300) {
			vibratorService.vibrate(
				VibrationEffect.createOneShot(
					millis,
					VibrationEffect.DEFAULT_AMPLITUDE
				)
			)
	}

}