package com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.dashboards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficerDashboard(
    grievances: List<Grievance>,
    onUpdateStatus: (String, GrievanceStatus) -> Unit,
    userEmail: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var proofDialogGrievanceId by remember { mutableStateOf<String?>(null) }
    var resolutionNotes by remember { mutableStateOf("") }

    val assignedGrievances = grievances.filter { 
        it.status != GrievanceStatus.SUBMITTED 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Department Officer Portal", fontWeight = FontWeight.Bold)
                        Text(userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Rounded.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Rounded.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = assignedGrievances.size.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Assigned Tasks", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = assignedGrievances.count { it.status == GrievanceStatus.RESOLVED }.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Resolved", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active & Pending (${assignedGrievances.count { it.status != GrievanceStatus.RESOLVED }})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Resolved (${assignedGrievances.count { it.status == GrievanceStatus.RESOLVED }})") }
                )
            }

            val filteredList = assignedGrievances.filter {
                if (selectedTab == 0) it.status != GrievanceStatus.RESOLVED else it.status == GrievanceStatus.RESOLVED
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No grievances found in this category.", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { grievance ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text(grievance.department, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Text(
                                        text = grievance.status.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(text = grievance.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = grievance.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (grievance.status != GrievanceStatus.IN_PROGRESS) {
                                        OutlinedButton(
                                            onClick = { onUpdateStatus(grievance.id, GrievanceStatus.IN_PROGRESS) },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("Set In Progress")
                                        }
                                    }
                                    if (grievance.status != GrievanceStatus.RESOLVED) {
                                        Button(
                                            onClick = { proofDialogGrievanceId = grievance.id }
                                        ) {
                                            Icon(Icons.Rounded.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Upload Resolution Proof")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Resolution Proof Dialog
    if (proofDialogGrievanceId != null) {
        AlertDialog(
            onDismissRequest = { proofDialogGrievanceId = null },
            title = { Text("Submit Resolution Proof") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide resolution notes and field inspection proof before marking as Resolved:")
                    OutlinedTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = { Text("Resolution Notes & Remarks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        proofDialogGrievanceId?.let { id ->
                            onUpdateStatus(id, GrievanceStatus.RESOLVED)
                        }
                        proofDialogGrievanceId = null
                        resolutionNotes = ""
                    }
                ) {
                    Text("Approve & Mark Resolved")
                }
            },
            dismissButton = {
                TextButton(onClick = { proofDialogGrievanceId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
