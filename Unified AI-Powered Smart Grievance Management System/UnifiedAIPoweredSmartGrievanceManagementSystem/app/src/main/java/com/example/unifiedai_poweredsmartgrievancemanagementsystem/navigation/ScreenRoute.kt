package com.example.unifiedai_poweredsmartgrievancemanagementsystem.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenRoute {
    @Serializable
    data object Home : ScreenRoute

    @Serializable
    data object Submission : ScreenRoute

    @Serializable
    data class Tracking(val grievanceId: String? = null) : ScreenRoute

    @Serializable
    data object Chat : ScreenRoute

    @Serializable
    data object Analytics : ScreenRoute
}
