package com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.dashboards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Departments
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictAdminDashboard(
    grievances: List<Grievance>,
    userEmail: String,
    onLogout: () -> Unit
) {
    val totalCount = grievances.size
    val resolvedCount = grievances.count { it.status == GrievanceStatus.RESOLVED }
    val pendingCount = grievances.count { it.status == GrievanceStatus.SUBMITTED }
    val slaCompliance = if (totalCount > 0) ((resolvedCount.toFloat() / totalCount) * 100).toInt() else 92

    val zones = listOf("North Zone", "South Zone", "East Zone", "West Zone", "Central Zone")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("District Administrator Portal", fontWeight = FontWeight.Bold)
                        Text("$userEmail (District HQ)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // District Heatmap & Metrics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("District Heatmap & Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Icon(Icons.Rounded.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Monitoring $totalCount total grievances across all district departments with $slaCompliance% SLA compliance.", style = MaterialTheme.typography.bodyMedium)
                        
                        LinearProgressIndicator(
                            progress = { slaCompliance / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            item {
                Text("District Zones Heatmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(zones) { zone ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(zone, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Active Grievances: ${grievances.size / zones.size + 3}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Badge(
                            containerColor = if (zone == "North Zone") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = if (zone == "North Zone") "High Load" else "Normal",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Text("Department Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(Departments.take(5)) { dept ->
                val deptCount = grievances.count { it.department.equals(dept, ignoreCase = true) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dept, fontWeight = FontWeight.Bold)
                        Text("$deptCount Grievances Filed", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
