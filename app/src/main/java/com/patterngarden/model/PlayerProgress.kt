package com.patterngarden.model

data class PlayerProgress(
    val levelStars: Map<Int, Int> = emptyMap(),
    val hintsRemaining: Int = 5
) {
    fun highestUnlockedLevel(): Int {
        if (levelStars.isEmpty()) return 1
        return (levelStars.keys.maxOrNull() ?: 0) + 1
    }

    fun totalStars(): Int = levelStars.values.sum()
}
