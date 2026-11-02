package com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class UserRole(
    val displayName: String,
    val defaultEmail: String,
    val defaultPassword: String,
    val description: String
) {
    CITIZEN(
        "Citizen",
        "citizen@uasgms.demo",
        "Citizen@123",
        "Submit complaints, view history, and chat with AI assistant"
    ),
    OFFICER(
        "Department Officer",
        "officer@uai-sgms.demo",
        "Officer@123",
        "Handle assigned complaints, update progress, upload resolution proof"
    ),
    DEPT_HEAD(
        "Department Head",
        "depthead@uai-sgms.demo",
        "DeptHead@123",
        "Manage officers, review escalated department grievances"
    ),
    DISTRICT_ADMIN(
        "District Administrator",
        "district@uai-sgms.demo",
        "District@123",
        "Monitor complaints across district departments & district heatmaps"
    ),
    STATE_ADMIN(
        "State Administrator",
        "state@uai-sgms.demo",
        "State@123",
        "Monitor statewide complaints, performance analytics, SLA compliance"
    ),
    SUPER_ADMIN(
        "Super Admin",
        "admin@uai-sgms.demo",
        "Admin@123",
        "Manage users, departments, roles, system settings, and AI configuration"
    )
}

data class AuthState(
    val isAuthenticated: Boolean = false,
    val email: String = "",
    val role: UserRole? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        _authState.update { it.copy(isLoading = true, errorMessage = null) }

        val trimmedEmail = email.trim()
        val matchedRole = UserRole.entries.find {
            it.defaultEmail.equals(trimmedEmail, ignoreCase = true) && it.defaultPassword == pass
        }

        if (matchedRole != null || (trimmedEmail.isNotBlank() && pass.isNotBlank())) {
            val assignedRole = matchedRole ?: when {
                trimmedEmail.contains("officer", true) -> UserRole.OFFICER
                trimmedEmail.contains("depthead", true) -> UserRole.DEPT_HEAD
                trimmedEmail.contains("district", true) -> UserRole.DISTRICT_ADMIN
                trimmedEmail.contains("state", true) -> UserRole.STATE_ADMIN
                trimmedEmail.contains("admin", true) -> UserRole.SUPER_ADMIN
                else -> UserRole.CITIZEN
            }

            _authState.update {
                AuthState(
                    isAuthenticated = true,
                    email = trimmedEmail,
                    role = assignedRole,
                    isLoading = false
                )
            }
            onSuccess()
        } else {
            _authState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Invalid credentials. Use demo buttons below for instant testing."
                )
            }
        }
    }

    fun quickLogin(role: UserRole) {
        _authState.update {
            AuthState(
                isAuthenticated = true,
                email = role.defaultEmail,
                role = role,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    fun logout() {
        _authState.update {
            AuthState(
                isAuthenticated = false,
                email = "",
                role = null,
                isLoading = false,
                errorMessage = null
            )
        }
    }
}
