package com.pydych.mobile.android.wordy.utils

class Sampler<T>(private val elements: List<Pair<T, Double>>) {
    private val normalizedScores: List<Double>
    private val random = kotlin.random.Random

    init {
        if (elements.isEmpty()) {
            normalizedScores = emptyList()
        } else {
            val minScore = elements.minOf { it.second }
            val shift = if (minScore < 0) -minScore else 0.0

            // Calculate prefix sums of shifted scores
            var sum = 0.0
            normalizedScores = elements.map {
                val shiftedScore = it.second + shift
                sum += shiftedScore
                sum
            }
        }
    }

    fun sample(): T? {
        if (elements.isEmpty()) return null
        if (elements.size == 1) return elements[0].first

        if (normalizedScores.last() == 0.0) {
            return elements[random.nextInt(elements.size)].first
        }

        val r = random.nextDouble() * normalizedScores.last()
        val idx = normalizedScores.binarySearch(r)

        // binarySearch returns -insertion_point - 1 if the element is not found
        return elements[if (idx < 0) (-idx - 1) else idx].first
    }
}
