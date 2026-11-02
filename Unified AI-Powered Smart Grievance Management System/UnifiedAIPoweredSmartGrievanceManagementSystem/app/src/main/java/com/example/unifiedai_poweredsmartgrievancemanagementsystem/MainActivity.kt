package com.example.unifiedai_poweredsmartgrievancemanagementsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.LoginScreen
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.dashboards.*
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.theme.UnifiedAIPoweredSmartGrievanceManagementSystemTheme
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel.AuthViewModel
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel.GrievanceViewModel
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel.UserRole

class MainActivity : ComponentActivity() {
    private val grievanceViewModel: GrievanceViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnifiedAIPoweredSmartGrievanceManagementSystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val grievances by grievanceViewModel.grievances.collectAsState()
                    val isSubmitting by grievanceViewModel.isSubmitting.collectAsState()
                    val authState by authViewModel.authState.collectAsState()

                    if (!authState.isAuthenticated || authState.role == null) {
                        LoginScreen(
                            errorMessage = authState.errorMessage,
                            onLogin = { email, password ->
                                authViewModel.login(email, password)
                            },
                            onQuickLogin = { role ->
                                authViewModel.quickLogin(role)
                            }
                        )
                    } else {
                        when (authState.role) {
                            UserRole.CITIZEN -> CitizenDashboard(
                                grievances = grievances,
                                isSubmitting = isSubmitting,
                                onSubmitGrievance = { title, desc, dept, lat, lng, loc, media, onSuccess ->
                                    grievanceViewModel.submitGrievance(
                                        title = title,
                                        description = desc,
                                        selectedDepartment = dept,
                                        latitude = lat,
                                        longitude = lng,
                                        locationName = loc,
                                        mediaUri = media,
                                        onSuccess = onSuccess
                                    )
                                },
                                onUpdateStatus = { id, status ->
                                    grievanceViewModel.updateGrievanceStatus(id, status)
                                },
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            UserRole.OFFICER -> OfficerDashboard(
                                grievances = grievances,
                                onUpdateStatus = { id, status ->
                                    grievanceViewModel.updateGrievanceStatus(id, status)
                                },
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            UserRole.DEPT_HEAD -> DeptHeadDashboard(
                                grievances = grievances,
                                onUpdateStatus = { id, status ->
                                    grievanceViewModel.updateGrievanceStatus(id, status)
                                },
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            UserRole.DISTRICT_ADMIN -> DistrictAdminDashboard(
                                grievances = grievances,
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            UserRole.STATE_ADMIN -> StateAdminDashboard(
                                grievances = grievances,
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            UserRole.SUPER_ADMIN -> SuperAdminDashboard(
                                userEmail = authState.email,
                                onLogout = { authViewModel.logout() }
                            )
                            else -> {
                                LoginScreen(
                                    errorMessage = authState.errorMessage,
                                    onLogin = { email, password -> authViewModel.login(email, password) },
                                    onQuickLogin = { role -> authViewModel.quickLogin(role) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
