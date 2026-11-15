package com.pararam2006.tseletrack.domain.career

import kotlinx.serialization.Serializable

data class CareerQuestion(
    val id: String,
    val title: String,
    val maxSelections: Int = 1,
    val options: List<CareerOption>,
)

data class CareerOption(
    val id: String,
    val title: String,
    val scores: Map<String, Int>,
)

data class CareerDirection(
    val id: String,
    val title: String,
    val profileTitle: String,
    val profileDescription: String,
    val institute: String,
    val programs: List<String>,
    val professions: List<String>,
    val strengths: List<String>,
)

data class CareerMatch(
    val direction: CareerDirection,
    val percent: Int,
)

data class CareerGuidanceResult(
    val matches: List<CareerMatch>,
) {
    val primaryMatch: CareerMatch
        get() = matches.first()
}

@Serializable
data class CareerMatchSnapshot(
    val directionId: String,
    val percent: Int,
)

@Serializable
data class CareerGuidanceSnapshot(
    val questionIndex: Int = 0,
    val answers: Map<String, List<String>> = emptyMap(),
    val matches: List<CareerMatchSnapshot> = emptyList(),
    val isSynced: Boolean = false,
)
