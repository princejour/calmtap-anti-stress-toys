package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ads.AdManager
import com.example.data.AppDatabase
import com.example.data.UserRepository
import com.example.ui.CalmApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodels.CalmViewModel
import com.example.viewmodels.CalmViewModelFactory
import com.google.android.gms.ads.MobileAds

import com.example.utils.SoundManager

class MainActivity : ComponentActivity() {

    private lateinit var adManager: AdManager
    private lateinit var viewModel: CalmViewModel
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this) {}
        adManager = AdManager(this)
        soundManager = SoundManager(this)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userStatsDao())
        viewModel = ViewModelProvider(this, CalmViewModelFactory(repository))[CalmViewModel::class.java]

        setContent {
            val stats by viewModel.userStats.collectAsState()
            soundManager.updateSettings(stats.soundEnabled, stats.musicEnabled, stats.sfxEnabled)
            
            MyApplicationTheme(darkTheme = stats.darkModeEnabled) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalmApp(viewModel = viewModel, adManager = adManager, soundManager = soundManager)
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (::soundManager.isInitialized) {
            soundManager.playMusic()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (::soundManager.isInitialized) {
            soundManager.pauseMusic()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) {
            soundManager.release()
        }
    }
}
