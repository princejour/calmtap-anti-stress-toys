package com.example.utils

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.SystemClock
import android.util.Log
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

class SoundManager(private val context: android.content.Context) {
    @Volatile private var ambientRunning = false
    @Volatile private var released = false

    private var ambientThread: Thread? = null
    private var ambientTrack: AudioTrack? = null
    private val lastPlayedAt = mutableMapOf<String, Long>()

    var isMasterEnabled = true
    var isMusicEnabled = true
    var isSfxEnabled = true

    fun updateSettings(master: Boolean, music: Boolean, sfx: Boolean) {
        isMasterEnabled = master
        isMusicEnabled = music
        isSfxEnabled = sfx

        if (isMasterEnabled && isMusicEnabled) {
            playMusic()
        } else {
            pauseMusic()
        }
    }

    fun playMusic() {
        if (!isMasterEnabled || !isMusicEnabled || released || ambientRunning) return
        ambientRunning = true

        ambientThread = thread(name = "CalmTapAmbientAudio", isDaemon = true) {
            val sampleRate = 44100
            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferFrames = 1024
            val buffer = ShortArray(bufferFrames * 2)

            try {
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    max(minBuffer, buffer.size * 2),
                    AudioTrack.MODE_STREAM
                )
                ambientTrack = track
                track.play()

                var frame = 0L
                while (ambientRunning && !released) {
                    var i = 0
                    while (i < bufferFrames) {
                        val t = frame.toDouble() / sampleRate.toDouble()

                        // Soft generated ambient pad. No external/copyrighted files.
                        val breath = 0.55 + 0.45 * sin(2.0 * PI * 0.035 * t)
                        val pad =
                            sin(2.0 * PI * 110.0 * t) * 0.45 +
                            sin(2.0 * PI * 146.83 * t) * 0.30 +
                            sin(2.0 * PI * 196.0 * t) * 0.20 +
                            sin(2.0 * PI * 329.63 * t) * 0.08
                        val water = sin(2.0 * PI * 0.18 * t) * sin(2.0 * PI * 440.0 * t) * 0.04
                        val sample = ((pad + water) * breath * 0.11 * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()

                        buffer[i * 2] = sample
                        buffer[i * 2 + 1] = sample
                        frame++
                        i++
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing generated ambient audio", e)
            } finally {
                try {
                    ambientTrack?.pause()
                    ambientTrack?.flush()
                    ambientTrack?.release()
                } catch (_: Exception) {
                }
                ambientTrack = null
                ambientRunning = false
            }
        }
    }

    fun pauseMusic() {
        ambientRunning = false
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error pausing generated ambient audio", e)
        }
    }

    fun playSfx(name: String, volume: Float = 1f) {
        if (!isMasterEnabled || !isSfxEnabled || released) return

        val now = SystemClock.elapsedRealtime()
        val throttleMs = when (name) {
            "spin" -> 160L
            "sand", "slime" -> 120L
            else -> 55L
        }
        val last = lastPlayedAt[name] ?: 0L
        if (now - last < throttleMs) return
        lastPlayedAt[name] = now

        when (name) {
            "pop" -> playGeneratedTone(620.0, 62, 0.42f * volume, 0.55)
            "bubble" -> playGeneratedTone(740.0, 55, 0.38f * volume, 0.50)
            "squish" -> playGeneratedTone(155.0, 145, 0.34f * volume, 0.25)
            "slime" -> playGeneratedTone(115.0, 170, 0.30f * volume, 0.18)
            "sand" -> playGeneratedTone(245.0, 95, 0.22f * volume, 0.10)
            "spin" -> playGeneratedTone(285.0, 95, 0.22f * volume, 0.20)
            "breathe" -> playGeneratedTone(432.0, 260, 0.26f * volume, 0.60)
            "rain" -> playGeneratedTone(360.0, 130, 0.18f * volume, 0.20)
            "water_drop" -> playGeneratedTone(820.0, 120, 0.35f * volume, 0.65)
            else -> playGeneratedTone(520.0, 70, 0.30f * volume, 0.45)
        }
    }

    private fun playGeneratedTone(frequency: Double, durationMs: Int, volume: Float, overtone: Double) {
        thread(name = "CalmTapSfxAudio", isDaemon = true) {
            val sampleRate = 44100
            val frames = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(frames)

            try {
                var seed = 1337
                for (i in 0 until frames) {
                    val t = i.toDouble() / sampleRate.toDouble()
                    val progress = i.toDouble() / frames.toDouble()
                    val envelope = sin(PI * progress).coerceAtLeast(0.0)

                    // Tiny deterministic noise layer makes touch sounds more tactile.
                    seed = seed xor (seed shl 13)
                    seed = seed xor (seed ushr 17)
                    seed = seed xor (seed shl 5)
                    val noise = ((seed and 0xFF) / 255.0 - 0.5) * 0.05

                    val wave =
                        sin(2.0 * PI * frequency * t) * 0.75 +
                        sin(2.0 * PI * frequency * 1.5 * t) * overtone * 0.25 +
                        noise

                    buffer[i] = (wave * envelope * volume * Short.MAX_VALUE)
                        .toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                }

                val minBuffer = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    max(minBuffer, buffer.size * 2),
                    AudioTrack.MODE_STREAM
                )
                track.play()
                track.write(buffer, 0, buffer.size)
                track.stop()
                track.release()
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing generated sound effect", e)
            }
        }
    }

    fun release() {
        released = true
        ambientRunning = false
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
            ambientTrack?.release()
            ambientTrack = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing generated audio", e)
        }
    }
}
