package com.squaregarden.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * Generates game sound effects procedurally using sine-wave synthesis.
 * No audio asset files needed.
 */
object SoundGenerator {

    private const val SAMPLE_RATE = 22050

    private fun generatePcm(
        durationMs: Int,
        builder: (sampleIndex: Int, totalSamples: Int) -> Float
    ): ShortArray {
        val totalSamples = SAMPLE_RATE * durationMs / 1000
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val sample = builder(i, totalSamples).coerceIn(-1f, 1f)
            pcm[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    private fun sine(freq: Float, sampleIndex: Int): Float {
        return sin(2.0 * PI * freq * sampleIndex / SAMPLE_RATE).toFloat()
    }

    private fun envelope(sampleIndex: Int, totalSamples: Int, attackMs: Int = 10, releaseMs: Int = 50): Float {
        val attackSamples = SAMPLE_RATE * attackMs / 1000
        val releaseSamples = SAMPLE_RATE * releaseMs / 1000
        val t = sampleIndex.toFloat()
        return when {
            sampleIndex < attackSamples -> t / attackSamples
            sampleIndex > totalSamples - releaseSamples -> (totalSamples - t) / releaseSamples
            else -> 1f
        }
    }

    /** Quick soft tap blip */
    fun generateTap(): ShortArray = generatePcm(80) { i, total ->
        val env = envelope(i, total, 5, 40)
        env * 0.5f * sine(880f, i)
    }

    /** Smooth slide whoosh */
    fun generateSwap(): ShortArray = generatePcm(200) { i, total ->
        val env = envelope(i, total, 15, 80)
        val progress = i.toFloat() / total
        val freq = 300f + 400f * progress
        env * 0.4f * (sine(freq, i) * 0.6f + sine(freq * 1.5f, i) * 0.4f)
    }

    /** Satisfying match ding */
    fun generateMatch(): ShortArray = generatePcm(350) { i, total ->
        val env = envelope(i, total, 10, 150)
        env * 0.6f * (sine(660f, i) * 0.5f + sine(990f, i) * 0.3f + sine(1320f, i) * 0.2f)
    }

    /** 1-star: modest ascending two-note chime */
    fun generateWin1Star(): ShortArray = generatePcm(600) { i, total ->
        val env = envelope(i, total, 20, 200)
        val progress = i.toFloat() / total
        val freq = if (progress < 0.5f) 440f else 554f // A4 → C#5
        env * 0.5f * (sine(freq, i) * 0.7f + sine(freq * 2f, i) * 0.3f)
    }

    /** 2-star: three-note ascending arpeggio with shimmer */
    fun generateWin2Star(): ShortArray = generatePcm(900) { i, total ->
        val env = envelope(i, total, 20, 300)
        val progress = i.toFloat() / total
        val freq = when {
            progress < 0.33f -> 440f   // A4
            progress < 0.66f -> 554f   // C#5
            else -> 659f               // E5
        }
        val shimmer = 1f + 0.003f * sine(6f, i)
        env * 0.55f * (sine(freq * shimmer, i) * 0.5f + sine(freq * 2f, i) * 0.3f + sine(freq * 3f, i) * 0.2f)
    }

    /** 3-star: grand fanfare with harmonics, sweep, and sparkle */
    fun generateWin3Star(): ShortArray = generatePcm(1400) { i, total ->
        val env = envelope(i, total, 30, 500)
        val progress = i.toFloat() / total
        val freq = when {
            progress < 0.2f -> 440f    // A4
            progress < 0.35f -> 554f   // C#5
            progress < 0.5f -> 659f    // E5
            progress < 0.7f -> 880f    // A5
            else -> 1108f              // C#6
        }
        val vibrato = 1f + 0.004f * sine(5f, i)
        val sparkle = if (progress > 0.5f) 0.15f * sine(3520f, i) * (1f - progress) else 0f
        env * 0.6f * (
            sine(freq * vibrato, i) * 0.4f +
            sine(freq * 2f, i) * 0.25f +
            sine(freq * 3f, i) * 0.15f +
            sine(freq * 0.5f, i) * 0.2f +
            sparkle
        )
    }

    /** Rising sparkle chime for each star landing on the counter */
    fun generateStarCollect(): ShortArray = generatePcm(150) { i, total ->
        val env = envelope(i, total, 5, 80)
        val progress = i.toFloat() / total
        val freq = 1200f + 600f * progress // rising sparkle
        env * 0.5f * (sine(freq, i) * 0.5f + sine(freq * 2f, i) * 0.3f + sine(freq * 3f, i) * 0.2f)
    }

    /** Sad descending tone for losing */
    fun generateLose(): ShortArray = generatePcm(700) { i, total ->
        val env = envelope(i, total, 20, 300)
        val progress = i.toFloat() / total
        val freq = 440f - 120f * progress
        env * 0.45f * (sine(freq, i) * 0.6f + sine(freq * 0.5f, i) * 0.4f)
    }

    suspend fun playPcm(pcm: ShortArray, volume: Float = 1f) = withContext(Dispatchers.IO) {
        val bufSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(bufSize, pcm.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        track.setVolume(volume)
        track.play()

        // Wait for playback to finish, then release
        val durationMs = (pcm.size.toLong() * 1000) / SAMPLE_RATE
        kotlinx.coroutines.delay(durationMs + 50)
        track.stop()
        track.release()
    }
}
