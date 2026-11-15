package com.pararam2006.tseletrack.domain.career

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerGuidanceEngineTest {
    private val engine = CareerGuidanceEngine()

    @Test
    fun `digital answers recommend TulSU IT programs`() {
        val itDirection = CareerGuidanceContent.directions.first()
        val answers = CareerGuidanceContent.questions.associate { question ->
            val bestOption = question.options.maxBy { it.scores[itDirection.id] ?: 0 }
            question.id to setOf(bestOption.id)
        }

        val result = engine.calculate(answers)

        assertEquals(itDirection.id, result.primaryMatch.direction.id)
        assertEquals(92, result.primaryMatch.percent)
        assertTrue(result.primaryMatch.direction.programs.any { it.startsWith("09.03.04") })
    }

    @Test
    fun `catalog contains only official TulSU institutes used by recommendations`() {
        val tulSuInstitutes = setOf(
            "Институт высокоточных систем им. В. П. Грязева",
            "Институт прикладной математики и компьютерных наук",
            "Институт строительства и архитектуры",
            "Политехнический институт",
            "Естественно-научный институт",
            "Институт педагогики, физической культуры, спорта и туризма",
            "Институт права и управления",
            "Институт гуманитарных и социальных наук",
            "Медицинский институт",
        )

        assertEquals(10, CareerGuidanceContent.directions.size)
        assertTrue(CareerGuidanceContent.directions.all { it.institute in tulSuInstitutes })
        assertTrue(CareerGuidanceContent.directions.all { it.programs.isNotEmpty() })
        assertTrue(CareerGuidanceContent.directions.all { it.professions.isNotEmpty() })
    }

    @Test
    fun `saved matches restore without recalculating answers`() {
        val snapshots = listOf(
            CareerMatchSnapshot("medicine", 92),
            CareerMatchSnapshot("natural", 76),
        )

        val restored = engine.restore(snapshots)

        assertEquals("medicine", restored?.primaryMatch?.direction?.id)
        assertEquals(76, restored?.matches?.get(1)?.percent)
    }
}
