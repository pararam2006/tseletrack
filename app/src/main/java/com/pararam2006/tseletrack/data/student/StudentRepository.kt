package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection

interface StudentRepository {
    suspend fun loadDashboard(): StudentDashboard
    suspend fun signContract()
    suspend fun loadTargetOffers(forceRefresh: Boolean = false): TargetOfferCatalog
    suspend fun loadTargetOfferSelection(): TargetOfferSelection?
    suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection
    suspend fun loadAdmissionStatus(): AdmissionStatus?
    suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus
    suspend fun refreshAdmissionStatus(): AdmissionStatus
    suspend fun updateTask(taskId: String, isCompleted: Boolean)
    suspend fun addTask()
    suspend fun completeCareerGuidance()
    suspend fun logout()
}
