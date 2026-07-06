package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UserRepository
import com.example.data.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalmViewModel(private val repository: UserRepository) : ViewModel() {

    val userStats: StateFlow<UserStats> = repository.userStats
        .map { it ?: UserStats() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

    private val _currentToyIndex = MutableStateFlow(0)
    val currentToyIndex: StateFlow<Int> = _currentToyIndex

    fun updateToyIndex(index: Int) {
        _currentToyIndex.value = index
    }

    fun toggleSound() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(soundEnabled = !current.soundEnabled))
    }

    fun toggleVibration() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(vibrationEnabled = !current.vibrationEnabled))
    }

    fun toggleDarkMode() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(darkModeEnabled = !current.darkModeEnabled))
    }

    fun toggleMusic() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(musicEnabled = !current.musicEnabled))
    }

    fun toggleSfx() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(sfxEnabled = !current.sfxEnabled))
    }

    fun addCoins(amount: Int) = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(calmCoins = current.calmCoins + amount))
    }

    fun incrementFailedAttempts() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(failedAttempts = current.failedAttempts + 1))
    }

    fun resetFailedAttempts() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(failedAttempts = 0))
    }

    fun completeStage() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(
            currentStage = current.currentStage + 1, 
            failedAttempts = 0,
            tempThemeUnlocked = false,
            tempSoundUnlocked = false,
            tempSkinUnlocked = false
        ))
    }

    fun unlockTempTheme() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(tempThemeUnlocked = true))
    }

    fun unlockTempSound() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(tempSoundUnlocked = true))
    }

    fun unlockTempSkin() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(current.copy(tempSkinUnlocked = true))
    }

    fun resetProgress() = viewModelScope.launch {
        val current = userStats.value
        repository.updateStats(UserStats(
            soundEnabled = current.soundEnabled, 
            musicEnabled = current.musicEnabled,
            sfxEnabled = current.sfxEnabled,
            vibrationEnabled = current.vibrationEnabled, 
            darkModeEnabled = current.darkModeEnabled
        ))
    }
}
