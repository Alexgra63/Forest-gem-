package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LevelRecord
import com.example.model.LevelRepository
import com.example.model.PowerUpType

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gem_forest_prefs", Context.MODE_PRIVATE)

    var coins: Int
        get() = prefs.getInt("player_coins", 150)
        set(value) = prefs.edit().putInt("player_coins", value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean("haptics_enabled", true)
        set(value) = prefs.edit().putBoolean("haptics_enabled", value).apply()

    var zenHighScore: Int
        get() = prefs.getInt("zen_high_score", 0)
        set(value) = prefs.edit().putInt("zen_high_score", value).apply()

    fun getPowerUpCount(type: PowerUpType): Int {
        // Starter gift of 2 for each powerup
        return prefs.getInt("powerup_${type.id}", 2)
    }

    fun setPowerUpCount(type: PowerUpType, count: Int) {
        prefs.edit().putInt("powerup_${type.id}", count.coerceAtLeast(0)).apply()
    }

    fun addPowerUp(type: PowerUpType, amount: Int = 1) {
        setPowerUpCount(type, getPowerUpCount(type) + amount)
    }

    fun usePowerUp(type: PowerUpType): Boolean {
        val current = getPowerUpCount(type)
        if (current > 0) {
            setPowerUpCount(type, current - 1)
            return true
        }
        return false
    }

    fun getLevelRecord(levelNumber: Int): LevelRecord {
        val stars = prefs.getInt("level_${levelNumber}_stars", 0)
        val highScore = prefs.getInt("level_${levelNumber}_high_score", 0)
        val isUnlocked = if (levelNumber == 1) true else prefs.getBoolean("level_${levelNumber}_unlocked", false)
        return LevelRecord(levelNumber, stars, highScore, isUnlocked)
    }

    fun getHighestUnlockedLevel(): Int {
        var highest = prefs.getInt("highest_unlocked_level", 1)
        while (getLevelRecord(highest + 1).isUnlocked) {
            highest++
        }
        return highest
    }

    fun saveLevelCompletion(levelNumber: Int, stars: Int, score: Int) {
        val currentRecord = getLevelRecord(levelNumber)
        val editor = prefs.edit()

        if (stars > currentRecord.stars) {
            editor.putInt("level_${levelNumber}_stars", stars)
        }
        if (score > currentRecord.highScore) {
            editor.putInt("level_${levelNumber}_high_score", score)
        }

        // Unlock next level on the endless grape vine if won
        if (stars > 0) {
            editor.putBoolean("level_${levelNumber + 1}_unlocked", true)
            val currentHighest = prefs.getInt("highest_unlocked_level", 1)
            if (levelNumber + 1 > currentHighest) {
                editor.putInt("highest_unlocked_level", levelNumber + 1)
            }
        }

        editor.apply()
    }

    fun getAllLevelRecords(): List<LevelRecord> {
        val highest = getHighestUnlockedLevel()
        val totalToShow = (highest + 10).coerceAtLeast(18)
        return (1..totalToShow).map { getLevelRecord(it) }
    }
}
