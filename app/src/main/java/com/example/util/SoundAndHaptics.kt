package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Handles tactile haptic feedback and synthesized magical forest audio chimes.
 */
class SoundAndHaptics(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    // Pentatonic scale frequencies for magical forest chimes (Hz)
    private val chimeFrequencies = listOf(
        523.25f, // C5
        587.33f, // D5
        659.25f, // E5
        783.99f, // G5
        880.00f, // A5
        1046.50f, // C6
        1174.66f, // D6
        1318.51f  // E6
    )

    fun playTileSelect() {
        vibrateLight()
        playChime(659.25f, durationMs = 45, amplitude = 0.25f)
    }

    fun playMatch(comboCount: Int) {
        vibrateMedium()
        val noteIndex = (comboCount - 1).coerceIn(0, chimeFrequencies.size - 1)
        val freq = chimeFrequencies[noteIndex]
        playChime(freq, durationMs = 120, amplitude = 0.5f)
    }

    fun playSpecialCreation() {
        vibrateDouble()
        playDualChime(783.99f, 1046.50f, durationMs = 160)
    }

    fun playExplosion() {
        vibrateHeavy()
        playNoiseBurst(durationMs = 150)
    }

    fun playPowerUpUsed() {
        vibrateDouble()
        playArpeggio()
    }

    fun playLevelWon() {
        vibrateCelebration()
        audioScope.launch {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            notes.forEach { note ->
                playChime(note, durationMs = 140, amplitude = 0.6f)
                kotlinx.coroutines.delay(110)
            }
        }
    }

    fun playLevelLost() {
        vibrateHeavy()
        audioScope.launch {
            val notes = listOf(440.0f, 392.0f, 349.23f)
            notes.forEach { note ->
                playChime(note, durationMs = 180, amplitude = 0.4f)
                kotlinx.coroutines.delay(150)
            }
        }
    }

    // --- Haptics ---
    private fun vibrateLight() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateMedium() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateDouble() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 25, 40, 35), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(70)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateHeavy() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(80)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateCelebration() {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 70, 50, 70, 100), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        } catch (_: Exception) {}
    }

    // --- Audio Synthesis ---
    private fun playChime(frequency: Float, durationMs: Int, amplitude: Float = 0.5f) {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Sine wave with bell decay envelope
                    val envelope = kotlin.math.exp(-time * 12.0)
                    val value = sin(2.0 * Math.PI * frequency * time) * envelope * amplitude
                    samples[i] = (value * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                writeAudio(samples, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun playDualChime(f1: Float, f2: Float, durationMs: Int) {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = kotlin.math.exp(-time * 10.0)
                    val s1 = sin(2.0 * Math.PI * f1 * time)
                    val s2 = sin(2.0 * Math.PI * f2 * time)
                    val value = (s1 * 0.5 + s2 * 0.5) * envelope * 0.6
                    samples[i] = (value * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                writeAudio(samples, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun playArpeggio() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val notes = listOf(587.33f, 783.99f, 1046.50f, 1318.51f)
            notes.forEach { n ->
                playChime(n, durationMs = 80, amplitude = 0.45f)
                kotlinx.coroutines.delay(60)
            }
        }
    }

    private fun playNoiseBurst(durationMs: Int) {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0f)).toInt()
                val samples = ShortArray(numSamples)

                var lastRandom = 0.0
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = kotlin.math.exp(-time * 18.0)
                    // Low-pass filtered noise + sub-bass punch
                    val white = (Math.random() * 2.0 - 1.0)
                    lastRandom = (lastRandom * 0.85) + (white * 0.15)
                    val bass = sin(2.0 * Math.PI * 90.0 * time) * 0.6
                    val value = (lastRandom * 0.4 + bass * 0.6) * envelope * 0.8
                    samples[i] = (value * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                writeAudio(samples, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun writeAudio(samples: ShortArray, sampleRate: Int) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        audioScope.launch {
            kotlinx.coroutines.delay(600)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
