package com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.navigation.ScreenRoute
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ui.screens.*

sealed class NavigationItem(val route: ScreenRoute, val title: String, val icon: ImageVector) {
    data object Home : NavigationItem(ScreenRoute.Home, "Home", Icons.Rounded.Home)
    data object Submission : NavigationItem(ScreenRoute.Submission, "Submit", Icons.Rounded.AddCircle)
    data object Tracking : NavigationItem(ScreenRoute.Tracking(), "Tracking", Icons.Rounded.TrackChanges)
    data object Chat : NavigationItem(ScreenRoute.Chat, "AI Chat", Icons.Rounded.Chat)
    data object Analytics : NavigationItem(ScreenRoute.Analytics, "Analytics", Icons.Rounded.Analytics)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
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
    val backStack = remember { mutableStateListOf<Any>(ScreenRoute.Home) }
    val currentRoute = backStack.lastOrNull() ?: ScreenRoute.Home

    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Submission,
        NavigationItem.Tracking,
        NavigationItem.Chat,
        NavigationItem.Analytics
    )

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpandedOrMedium = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Citizen Portal", fontWeight = FontWeight.Bold)
                        Text(userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        bottomBar = {
            if (!isExpandedOrMedium) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = when (item.route) {
                            is ScreenRoute.Home -> currentRoute is ScreenRoute.Home
                            is ScreenRoute.Submission -> currentRoute is ScreenRoute.Submission
                            is ScreenRoute.Tracking -> currentRoute is ScreenRoute.Tracking
                            is ScreenRoute.Chat -> currentRoute is ScreenRoute.Chat
                            is ScreenRoute.Analytics -> currentRoute is ScreenRoute.Analytics
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    backStack.add(item.route)
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Adaptive Navigation Rail for medium/expanded screens (tablets, foldables)
            if (isExpandedOrMedium) {
                NavigationRail {
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEach { item ->
                        val selected = when (item.route) {
                            is ScreenRoute.Home -> currentRoute is ScreenRoute.Home
                            is ScreenRoute.Submission -> currentRoute is ScreenRoute.Submission
                            is ScreenRoute.Tracking -> currentRoute is ScreenRoute.Tracking
                            is ScreenRoute.Chat -> currentRoute is ScreenRoute.Chat
                            is ScreenRoute.Analytics -> currentRoute is ScreenRoute.Analytics
                        }
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    backStack.add(item.route)
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is ScreenRoute.Home -> NavEntry(key) {
                                HomeScreen(
                                    grievances = grievances,
                                    onNavigateToSubmission = { backStack.add(ScreenRoute.Submission) },
                                    onNavigateToTracking = { id -> backStack.add(ScreenRoute.Tracking(id)) },
                                    onNavigateToAnalytics = { backStack.add(ScreenRoute.Analytics) },
                                    onNavigateToChat = { backStack.add(ScreenRoute.Chat) }
                                )
                            }
                            is ScreenRoute.Submission -> NavEntry(key) {
                                SubmissionScreen(
                                    isSubmitting = isSubmitting,
                                    onSubmitGrievance = onSubmitGrievance,
                                    onNavigateToTracking = { id ->
                                        backStack.add(ScreenRoute.Tracking(id))
                                    }
                                )
                            }
                            is ScreenRoute.Tracking -> NavEntry(key) {
                                TrackingScreen(
                                    grievances = grievances,
                                    initialGrievanceId = key.grievanceId,
                                    onUpdateStatus = onUpdateStatus
                                )
                            }
                            is ScreenRoute.Chat -> NavEntry(key) {
                                ChatScreen()
                            }
                            is ScreenRoute.Analytics -> NavEntry(key) {
                                AnalyticsScreen(grievances = grievances)
                            }
                            else -> NavEntry(key) {
                                Text("Unknown Route")
                            }
                        }
                    }
                )
            }
        }
    }
}
