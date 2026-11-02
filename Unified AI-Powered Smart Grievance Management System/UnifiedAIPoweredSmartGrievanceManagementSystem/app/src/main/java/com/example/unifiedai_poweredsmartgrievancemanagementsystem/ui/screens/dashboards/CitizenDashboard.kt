package com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.dashboards

import androidx.compose.runtime.Composable
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.components.AppScaffold

@Composable
fun CitizenDashboard(
    grievances: List<Grievance>,
    isSubmitting: Boolean,
    onSubmitGrievance: (
        title: String,
        description: String,
        department: String,
        latitude: Double?,
        longitude: Double?,
        locationName: String?,
        mediaUri: String?,
        onSuccess: (String) -> Unit
    ) -> Unit,
    onUpdateStatus: (String, GrievanceStatus) -> Unit,
    userEmail: String,
    onLogout: () -> Unit
) {
    AppScaffold(
        grievances = grievances,
        isSubmitting = isSubmitting,
        onSubmitGrievance = onSubmitGrievance,
        onUpdateStatus = onUpdateStatus,
        userEmail = userEmail,
        onLogout = onLogout
    )
}
