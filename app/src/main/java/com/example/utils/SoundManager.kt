package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.example.R

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool
    
    private val soundIds = mutableMapOf<String, Int>()
    
    var isMasterEnabled = true
    var isMusicEnabled = true
    var isSfxEnabled = true
    
    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
            
        // Load sounds
        try {
            soundIds["pop"] = soundPool.load(context, R.raw.sfx_pop, 1)
            soundIds["squish"] = soundPool.load(context, R.raw.sfx_squish, 1)
            soundIds["bubble"] = soundPool.load(context, R.raw.sfx_bubble, 1)
            soundIds["slime"] = soundPool.load(context, R.raw.sfx_slime, 1)
            soundIds["sand"] = soundPool.load(context, R.raw.sfx_sand, 1)
            soundIds["spin"] = soundPool.load(context, R.raw.sfx_spin, 1)
            soundIds["breathe"] = soundPool.load(context, R.raw.sfx_breathe, 1)
            soundIds["rain"] = soundPool.load(context, R.raw.sfx_rain, 1)
            soundIds["water_drop"] = soundPool.load(context, R.raw.sfx_water_drop, 1)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error loading sounds", e)
        }
    }
    
    fun updateSettings(master: Boolean, music: Boolean, sfx: Boolean) {
        isMasterEnabled = master
        isMusicEnabled = music
        isSfxEnabled = sfx
        
        if (!isMasterEnabled || !isMusicEnabled) {
            pauseMusic()
        } else {
            playMusic()
        }
    }
    
    fun playMusic() {
        if (!isMasterEnabled || !isMusicEnabled) return
        
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.bgm_ambient)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(0.3f, 0.3f)
            }
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing music", e)
        }
    }
    
    fun pauseMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error pausing music", e)
        }
    }
    
    fun playSfx(name: String, volume: Float = 1f) {
        if (!isMasterEnabled || !isSfxEnabled) return
        
        try {
            soundIds[name]?.let { soundId ->
                soundPool.play(soundId, volume, volume, 1, 0, 1f)
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing sfx", e)
        }
    }
    
    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing sounds", e)
        }
    }
}
