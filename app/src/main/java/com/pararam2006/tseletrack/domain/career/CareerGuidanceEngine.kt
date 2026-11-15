package com.pararam2006.tseletrack.domain.career

import kotlin.math.roundToInt

class CareerGuidanceEngine {
    fun calculate(answers: Map<String, Set<String>>): CareerGuidanceResult {
        val scores = CareerGuidanceContent.directions.associate { it.id to 0 }.toMutableMap()

        CareerGuidanceContent.questions.forEach { question ->
            val selected = answers[question.id].orEmpty()
            question.options
                .filter { it.id in selected }
                .flatMap { it.scores.entries }
                .forEach { (directionId, points) ->
                    scores[directionId] = scores.getValue(directionId) + points
                }
        }

        val ranked = CareerGuidanceContent.directions
            .map { direction -> direction to scores.getValue(direction.id) }
            .filter { (_, score) -> score > 0 }
            .sortedWith(compareByDescending<Pair<CareerDirection, Int>> { it.second }.thenBy { it.first.title })
            .take(3)
        val topScore = ranked.firstOrNull()?.second ?: 1
        val matches = ranked.map { (direction, score) ->
            CareerMatch(
                direction = direction,
                percent = ((score.toFloat() / topScore) * TOP_MATCH_PERCENT)
                    .roundToInt()
                    .coerceIn(MIN_MATCH_PERCENT, TOP_MATCH_PERCENT),
            )
        }
        return CareerGuidanceResult(matches)
    }

    fun restore(matches: List<CareerMatchSnapshot>): CareerGuidanceResult? {
        val restored = matches.mapNotNull { snapshot ->
            CareerGuidanceContent.directions
                .firstOrNull { it.id == snapshot.directionId }
                ?.let { CareerMatch(it, snapshot.percent) }
        }
        return restored.takeIf { it.isNotEmpty() }?.let(::CareerGuidanceResult)
    }

    private companion object {
        const val TOP_MATCH_PERCENT = 92
        const val MIN_MATCH_PERCENT = 45
    }
}
