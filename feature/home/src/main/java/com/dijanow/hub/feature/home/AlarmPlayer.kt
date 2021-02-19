package com.dijanow.hub.feature.home

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPlayer @Inject constructor(
	@ApplicationContext private val context: Context
) {

	private val soundPool: SoundPool by lazy {
		SoundPool.Builder()
			.setMaxStreams(1)
			.setAudioAttributes(
				AudioAttributes.Builder()
					.setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
					.setUsage(AudioAttributes.USAGE_ALARM)
					.build()
			)
			.build()
	}

	private val audioManager: AudioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
	private var currentlyPlaying: Int? = null

	fun playAlarm() {
		stopAlarm()
		soundPool.setOnLoadCompleteListener { _, sampleId, _ ->
			increaseVolume()
			currentlyPlaying = soundPool.play(sampleId, 1F, 1F, 1, -1, 1F)
			soundPool.setOnLoadCompleteListener(null)
		}
		soundPool.load(context, R.raw.alarm_new_order, 1)
	}

	fun stopAlarm() {
		currentlyPlaying?.let {
			soundPool.stop(it)
			currentlyPlaying = null
		}
	}

	fun isPlaying(): Boolean = currentlyPlaying != null

	private fun increaseVolume() {
		val currentVolume = audioManager.getStreamVolume(DEFAULT_STREAM_TYPE)
		val maxVolume = audioManager.getStreamMaxVolume(DEFAULT_STREAM_TYPE)

		val targetVolume: Int = (maxVolume / 2).toInt() // 50%
		if (currentVolume < targetVolume) {
			audioManager.setStreamVolume(DEFAULT_STREAM_TYPE, targetVolume, 0)
		}
	}

	companion object {
		const val DEFAULT_STREAM_TYPE = AudioManager.STREAM_ALARM
	}

}