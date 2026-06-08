package com.example.flashcards

import kotlin.math.roundToInt

object Scheduler {
    fun calculateNewProgress(quality: Int, current: CardProgress?): CardProgress {
        val currentProgress = current ?: CardProgress("")
        if (quality < 3) {
            // Forget
            return currentProgress.copy(
                intervalDays = 1,
                repetitions = 0,
                nextReviewDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
            )
        }
        val newEasiness = (currentProgress.easiness + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)))
            .coerceAtLeast(1.3)
        val newInterval = when {
            currentProgress.repetitions == 0 -> 1
            currentProgress.repetitions == 1 -> 6
            else -> (currentProgress.intervalDays * newEasiness).roundToInt()
        }
        return currentProgress.copy(
            easiness = newEasiness,
            intervalDays = newInterval,
            repetitions = currentProgress.repetitions + 1,
            nextReviewDate = System.currentTimeMillis() + newInterval * 24 * 60 * 60 * 1000L
        )
    }
}