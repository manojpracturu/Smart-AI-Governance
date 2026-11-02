package com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievancePriority
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel.TrackingDashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    grievances: List<Grievance>,
    initialGrievanceId: String?,
    onUpdateStatus: (String, GrievanceStatus) -> Unit,
    viewModel: TrackingDashboardViewModel = viewModel()
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val coroutineScope = rememberCoroutineScope()
    var selectedGrievanceId by remember(initialGrievanceId) {
        mutableStateOf(initialGrievanceId ?: grievances.firstOrNull()?.id)
    }
    var statusFilter by remember { mutableStateOf<GrievanceStatus?>(null) }
    val notifications by viewModel.notifications.collectAsState()
    val slaTimers by viewModel.slaTimers.collectAsState()

    var citizenFeedbackInput by remember { mutableStateOf("") }
    var feedbackSubmittedMap by remember { mutableStateOf(mapOf<String, String>()) }

    val filteredGrievances = remember(grievances, statusFilter) {
        if (statusFilter == null) grievances else grievances.filter { it.status == statusFilter }
    }

    val selectedGrievance = remember(grievances, selectedGrievanceId) {
        grievances.find { it.id == selectedGrievanceId } ?: grievances.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Real-Time Tracking Dashboard & SLA", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        viewModel.triggerMockPushNotification(
                            "Status Update Alert",
                            "Grievance GRV-1001 status advanced to In Progress."
                        )
                    }) {
                        Icon(Icons.Rounded.NotificationsActive, contentDescription = "Simulate Push Notification")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Real-time Notification Banner / Mock Push Notification System
            if (notifications.isNotEmpty()) {
                val notif = notifications.first()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(text = notif.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(text = notif.message, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = { viewModel.dismissNotification(notif.id) }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Material Adaptive ListDetailPaneScaffold
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    PaneScaffoldRoleWrapper {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Status Filter Chips: Submitted, Assigned, In Progress, Resolved, Escalated
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = statusFilter == null,
                                    onClick = { statusFilter = null },
                                    label = { Text("All") }
                                )
                                FilterChip(
                                    selected = statusFilter == GrievanceStatus.SUBMITTED,
                                    onClick = { statusFilter = GrievanceStatus.SUBMITTED },
                                    label = { Text("Submitted") }
                                )
                                FilterChip(
                                    selected = statusFilter == GrievanceStatus.ASSIGNED,
                                    onClick = { statusFilter = GrievanceStatus.ASSIGNED },
                                    label = { Text("Assigned") }
                                )
                                FilterChip(
                                    selected = statusFilter == GrievanceStatus.IN_PROGRESS,
                                    onClick = { statusFilter = GrievanceStatus.IN_PROGRESS },
                                    label = { Text("In Progress") }
                                )
                                FilterChip(
                                    selected = statusFilter == GrievanceStatus.RESOLVED,
                                    onClick = { statusFilter = GrievanceStatus.RESOLVED },
                                    label = { Text("Resolved") }
                                )
                                FilterChip(
                                    selected = statusFilter == GrievanceStatus.ESCALATED,
                                    onClick = { statusFilter = GrievanceStatus.ESCALATED },
                                    label = { Text("Escalated") }
                                )
                            }

                            if (filteredGrievances.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No grievances found for this status filter.", color = MaterialTheme.colorScheme.outline)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(filteredGrievances) { grievance ->
                                        val isSelected = grievance.id == selectedGrievanceId
                                        val remainingMins = slaTimers[grievance.id] ?: 1440
                                        val hours = remainingMins / 60
                                        val mins = remainingMins % 60

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedGrievanceId = grievance.id
                                                    coroutineScope.launch {
                                                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                                    }
                                                },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = grievance.id,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Badge(
                                                        containerColor = when (grievance.status) {
                                                            GrievanceStatus.RESOLVED -> MaterialTheme.colorScheme.tertiaryContainer
                                                            GrievanceStatus.ESCALATED -> MaterialTheme.colorScheme.errorContainer
                                                            else -> MaterialTheme.colorScheme.secondaryContainer
                                                        }
                                                    ) {
                                                        Text(text = grievance.status.displayName, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                                Text(
                                                    text = grievance.title,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = grievance.department,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "SLA: ${hours}h ${mins}m",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = if (remainingMins < 360) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                detailPane = {
                    PaneScaffoldRoleWrapper {
                        if (selectedGrievance != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedGrievance.id,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Badge(
                                            containerColor = when (selectedGrievance.priority) {
                                                GrievancePriority.CRITICAL -> MaterialTheme.colorScheme.error
                                                GrievancePriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                                else -> MaterialTheme.colorScheme.secondaryContainer
                                            }
                                        ) {
                                            Text(
                                                text = "Priority: ${selectedGrievance.priority.displayName}",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = selectedGrievance.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = selectedGrievance.description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Divider()

                                    // Visual Progress Timeline (Submission -> AI Routing -> Officer Assignment -> Investigation -> Resolution -> Citizen Feedback)
                                    ProgressTimeline(status = selectedGrievance.status)

                                    Divider()

                                    DetailRow(icon = Icons.Rounded.Business, label = "Department", value = selectedGrievance.department)
                                    DetailRow(icon = Icons.Rounded.Person, label = "Assigned Officer", value = selectedGrievance.assignedOfficer)
                                    DetailRow(icon = Icons.Rounded.LocationOn, label = "Location", value = selectedGrievance.locationName ?: "GPS Tagged")

                                    // Citizen Feedback Section if Resolved
                                    if (selectedGrievance.status == GrievanceStatus.RESOLVED) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("Citizen Feedback & Rating", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                val existingFeedback = feedbackSubmittedMap[selectedGrievance.id]
                                                if (existingFeedback != null) {
                                                    Text("Feedback submitted: \"$existingFeedback\"", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                } else {
                                                    OutlinedTextField(
                                                        value = citizenFeedbackInput,
                                                        onValueChange = { citizenFeedbackInput = it },
                                                        placeholder = { Text("Rate resolution quality & provide feedback...") },
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                    Button(
                                                        onClick = {
                                                            if (citizenFeedbackInput.isNotBlank()) {
                                                                feedbackSubmittedMap = feedbackSubmittedMap + (selectedGrievance.id to citizenFeedbackInput)
                                                                citizenFeedbackInput = ""
                                                            }
                                                        },
                                                        modifier = Modifier.align(Alignment.End)
                                                    ) {
                                                        Text("Submit Feedback")
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // Status update action buttons for officers/admin
                                    Text("Update Lifecycle Status:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onUpdateStatus(selectedGrievance.id, GrievanceStatus.ASSIGNED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                        ) {
                                            Text("Assign", color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        }
                                        Button(
                                            onClick = { onUpdateStatus(selectedGrievance.id, GrievanceStatus.IN_PROGRESS) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Text("In Progress", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = { onUpdateStatus(selectedGrievance.id, GrievanceStatus.RESOLVED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text("Resolve")
                                        }
                                        Button(
                                            onClick = { onUpdateStatus(selectedGrievance.id, GrievanceStatus.ESCALATED) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                        ) {
                                            Text("Escalate", color = MaterialTheme.colorScheme.onErrorContainer)
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Select a grievance from the list to view timeline & details", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PaneScaffoldRoleWrapper(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
    }
}

@Composable
fun ProgressTimeline(status: GrievanceStatus) {
    val steps = listOf(
        "Submission" to GrievanceStatus.SUBMITTED,
        "AI Routing" to GrievanceStatus.AI_CLASSIFIED,
        "Officer" to GrievanceStatus.ASSIGNED,
        "Investigation" to GrievanceStatus.IN_PROGRESS,
        "Resolution" to GrievanceStatus.RESOLVED,
        "Feedback" to GrievanceStatus.RESOLVED
    )

    val currentIndex = when (status) {
        GrievanceStatus.SUBMITTED -> 0
        GrievanceStatus.AI_CLASSIFIED -> 1
        GrievanceStatus.ASSIGNED -> 2
        GrievanceStatus.IN_PROGRESS -> 3
        GrievanceStatus.RESOLVED -> 5
        GrievanceStatus.ESCALATED -> 3
        GrievanceStatus.REJECTED -> 1
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("Lifecycle Progress Timeline", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, (label, _) ->
                val isCompleted = index <= currentIndex
                val isCurrent = index == currentIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isCompleted -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
        }
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
