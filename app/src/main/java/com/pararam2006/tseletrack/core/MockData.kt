package com.pararam2006.tseletrack.core

import com.pararam2006.tseletrack.data.api.dto.DealDto
import com.pararam2006.tseletrack.data.api.dto.EventDto
import com.pararam2006.tseletrack.data.api.dto.FunnelResponse

object MockData {
    val deals = listOf(
        DealDto(1, 1, 2, 3, currentStage = 4, status = "active"),
        DealDto(2, 4, 2, 3, currentStage = 2, status = "active"),
        DealDto(3, 5, 6, 3, currentStage = 6, status = "completed")
    )
    val events = listOf(
        EventDto(
            1,
            "university",
            "grade_added",
            "Студент сдал сессию на отлично",
            "2026-11-14T10:00"
        ),
        EventDto(2, "company", "feedback", "Ждём на практику в январе", "2026-11-14T11:30")
    )
    val funnel = FunnelResponse(
        total = 3,
        byStage = mapOf("1" to 0, "2" to 1, "3" to 0, "4" to 1, "5" to 0, "6" to 1)
    )
}