package com.pararam2006.tseletrack.data.api.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val role: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String,
    val role: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class DealDto(
    val id: Int,
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("university_id") val universityId: Int,
    @SerializedName("company_id") val companyId: Int,
    @SerializedName("current_stage") val currentStage: Int,
    val status: String,
    @SerializedName("prof_test_result") val profTestResult: String? = null
)

data class CreateDealRequest(
    @SerializedName("student_id") val studentId: Int,
    @SerializedName("university_id") val universityId: Int,
    @SerializedName("company_id") val companyId: Int
)

data class EventDto(
    val id: Int,
    @SerializedName("actor_role") val actorRole: String,
    @SerializedName("event_type") val eventType: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String
)

data class CreateEventRequest(
    @SerializedName("actor_role") val actorRole: String,
    @SerializedName("event_type") val eventType: String,
    val description: String
)

data class FunnelResponse(
    val total: Int,
    @SerializedName("by_stage") val byStage: Map<String, Int>
)

data class AdvanceResponse(
    val status: String,
    @SerializedName("current_stage") val currentStage: Int
)