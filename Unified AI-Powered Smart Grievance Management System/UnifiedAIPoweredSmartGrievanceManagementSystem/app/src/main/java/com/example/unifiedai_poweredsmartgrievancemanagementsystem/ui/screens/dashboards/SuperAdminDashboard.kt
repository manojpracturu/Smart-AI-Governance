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
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboard(
    userEmail: String,
    onLogout: () -> Unit
) {
    var aiConfidenceThreshold by remember { mutableFloatStateOf(0.92f) }
    var autoAssignmentEnabled by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Super Admin Portal", fontWeight = FontWeight.Bold)
                        Text("$userEmail (System Root)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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
                            Text("System Core Controls", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                        Text("Full administrative privilege to manage system settings, AI engine configuration, users, and roles.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Text("AI Model Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("AI Confidence Threshold: ${(aiConfidenceThreshold * 100).toInt()}%", fontWeight = FontWeight.Bold)
                        Slider(
                            value = aiConfidenceThreshold,
                            onValueChange = { aiConfidenceThreshold = it },
                            valueRange = 0.5f..1f
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Auto-Assignment AI Engine", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = autoAssignmentEnabled,
                                onCheckedChange = { autoAssignmentEnabled = it }
                            )
                        }
                    }
                }
            }

            item {
                Text("Role & Department Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(UserRole.entries) { role ->
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
                        Column {
                            Text(role.displayName, fontWeight = FontWeight.Bold)
                            Text(role.defaultEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text("Active", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            item {
                Text("Departments Configured (${Departments.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(Departments) { dept ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dept, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { /* edit dept */ }) {
                            Text("Configure")
                        }
                    }
                }
            }
        }
    }
}
