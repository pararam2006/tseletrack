package com.pararam2006.tseletrack.core

sealed interface Route {
    object Login: Route
    object Registration: Route
    object Company: Route
    object Student: Route
    object University: Route
    object Region: Route // навигация от Admin
}

sealed interface Role {
    object Company: Route
    object Student: Route
    object University: Route
    object Admin: Route // навигация на Region
}