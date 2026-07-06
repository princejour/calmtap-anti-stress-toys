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

class MainActivity : ComponentActivity() {

    private lateinit var adManager: AdManager
    private lateinit var viewModel: CalmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this) {}
        adManager = AdManager(this)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userStatsDao())
        viewModel = ViewModelProvider(this, CalmViewModelFactory(repository))[CalmViewModel::class.java]

        setContent {
            val stats by viewModel.userStats.collectAsState()
            MyApplicationTheme(darkTheme = stats.darkModeEnabled) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalmApp(viewModel = viewModel, adManager = adManager)
                }
            }
        }
    }
}
