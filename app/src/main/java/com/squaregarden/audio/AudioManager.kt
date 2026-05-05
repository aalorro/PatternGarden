package com.squaregarden.audio

import android.content.Context
import com.squaregarden.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AudioManager(private val context: Context) {

    private val settingsRepo = SettingsRepository(context)
    private var soundEnabled = true
    private var scope: CoroutineScope? = null

    // Pre-generate PCM data so playback is instant
    private val tapPcm by lazy { SoundGenerator.generateTap() }
    private val swapPcm by lazy { SoundGenerator.generateSwap() }
    private val matchPcm by lazy { SoundGenerator.generateMatch() }
    private val win1Pcm by lazy { SoundGenerator.generateWin1Star() }
    private val win2Pcm by lazy { SoundGenerator.generateWin2Star() }
    private val win3Pcm by lazy { SoundGenerator.generateWin3Star() }
    private val losePcm by lazy { SoundGenerator.generateLose() }
    private val starCollectPcm by lazy { SoundGenerator.generateStarCollect() }

    fun observeSettings(scope: CoroutineScope) {
        this.scope = scope
        scope.launch {
            settingsRepo.soundEnabled.collect { soundEnabled = it }
        }
    }

    private fun play(pcm: ShortArray, volume: Float = 0.7f) {
        if (!soundEnabled) return
        scope?.launch {
            SoundGenerator.playPcm(pcm, volume)
        }
    }

    fun playTap() = play(tapPcm, 0.5f)
    fun playSwap() = play(swapPcm, 0.7f)
    fun playMatch() = play(matchPcm, 0.8f)
    fun playWin(stars: Int = 1) {
        val pcm = when (stars) {
            3 -> win3Pcm
            2 -> win2Pcm
            else -> win1Pcm
        }
        play(pcm, 1f)
    }
    fun playLose() = play(losePcm, 0.8f)
    fun playStarCollect() = play(starCollectPcm, 0.6f)

    fun release() {
        // No SoundPool to release anymore
    }
}
