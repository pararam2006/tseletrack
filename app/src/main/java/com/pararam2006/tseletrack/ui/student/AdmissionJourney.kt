package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.AdmissionState

internal enum class AdmissionMilestoneType {
    OfferSelected,
    ApplicationFound,
    Recommended,
    Enrolled,
}

internal enum class AdmissionMilestoneStatus {
    Completed,
    Current,
    Upcoming,
}

internal data class AdmissionMilestone(
    val type: AdmissionMilestoneType,
    val status: AdmissionMilestoneStatus,
)

internal fun AdmissionState?.toAdmissionMilestones(): List<AdmissionMilestone> {
    val progress = when (this) {
        null,
        AdmissionState.Unknown,
        -> 0

        AdmissionState.ApplicationFound -> 1
        AdmissionState.Recommended -> 2
        AdmissionState.Enrolled -> 3
    }
    return AdmissionMilestoneType.entries.mapIndexed { index, type ->
        AdmissionMilestone(
            type = type,
            status = when {
                index == 0 || index <= progress -> AdmissionMilestoneStatus.Completed
                index == progress + 1 -> AdmissionMilestoneStatus.Current
                else -> AdmissionMilestoneStatus.Upcoming
            },
        )
    }
}
