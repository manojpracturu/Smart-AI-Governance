package com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class PushNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class TrackingDashboardViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<PushNotification>>(
        listOf(
            PushNotification(
                title = "Live Status Update",
                message = "Grievance GRV-1001 assigned to Er. Rajesh Kumar. Investigation underway."
            ),
            PushNotification(
                title = "SLA Countdown Active",
                message = "GRV-1004 SLA deadline approaching in 6 hours."
            )
        )
    )
    val notifications: StateFlow<List<PushNotification>> = _notifications.asStateFlow()

    private val _slaTimers = MutableStateFlow<Map<String, Int>>(
        mapOf(
            "GRV-1001" to 14 * 60 + 22, // minutes remaining
            "GRV-1002" to 36 * 60,
            "GRV-1003" to 42 * 60,
            "GRV-1004" to 5 * 60 + 45,
            "GRV-1005" to 0
        )
    )
    val slaTimers: StateFlow<Map<String, Int>> = _slaTimers.asStateFlow()

    init {
        // Simulate live countdown and periodic push notification
        viewModelScope.launch {
            while (true) {
                delay(30000L) // every 30 seconds simulate timer decrement
                _slaTimers.value = _slaTimers.value.mapValues { (_, mins) -> if (mins > 0) mins - 1 else 0 }
            }
        }
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun triggerMockPushNotification(title: String, message: String) {
        val newNotif = PushNotification(title = title, message = message)
        _notifications.value = listOf(newNotif) + _notifications.value
    }
}
