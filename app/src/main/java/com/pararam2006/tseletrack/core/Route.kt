package com.pararam2006.tseletrack.core

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Registration : Route

    @Serializable
    data object CareerGuidance : Route

    @Serializable
    data object Company : Route

    @Serializable
    data object Student : Route

    @Serializable
    data object University : Route

    @Serializable
    data object Admin : Route
}

enum class Role {
    Company,
    Student,
    University,
    Admin,
}
