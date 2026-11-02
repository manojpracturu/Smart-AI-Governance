package com.example.unifiedai_poweredsmartgrievancemanagementsystem.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GrievanceRepository {
    private val initialGrievances = listOf(
        Grievance(
            id = "GRV-1001",
            title = "Major Water Pipe Burst on 4th Main Road",
            description = "A large water pipeline has burst near the central market intersection, flooding the street and wasting thousands of liters.",
            department = "Water Supply",
            priority = GrievancePriority.HIGH,
            status = GrievanceStatus.IN_PROGRESS,
            latitude = 12.9716,
            longitude = 77.5946,
            locationName = "4th Main Road, Central Market",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            aiConfidence = 0.98f,
            aiSuggestedTags = listOf("Water Leak", "Pipe Burst", "Urgent Infrastructure"),
            assignedOfficer = "Er. Rajesh Kumar"
        ),
        Grievance(
            id = "GRV-1002",
            title = "Streetlights Non-Functional in Sector 7",
            description = "All streetlights along Park Avenue in Sector 7 have been out for 4 nights, causing safety concerns for pedestrians.",
            department = "Electricity",
            priority = GrievancePriority.MEDIUM,
            status = GrievanceStatus.ASSIGNED,
            latitude = 12.9820,
            longitude = 77.6010,
            locationName = "Park Avenue, Sector 7",
            timestamp = System.currentTimeMillis() - 86400000L,
            aiConfidence = 0.95f,
            aiSuggestedTags = listOf("Lighting", "Public Safety", "Electrical"),
            assignedOfficer = "Ms. Priya Sharma"
        ),
        Grievance(
            id = "GRV-1003",
            title = "Uncollected Garbage Accumulation near School",
            description = "Garbage bins haven't been cleared for 3 days near St. Mary's High School, creating severe health hazard and foul odor.",
            department = "Environment & Sanitation",
            priority = GrievancePriority.HIGH,
            status = GrievanceStatus.SUBMITTED,
            latitude = 12.9650,
            longitude = 77.5820,
            locationName = "St. Mary's School Lane",
            timestamp = System.currentTimeMillis() - 3600000L * 5,
            aiConfidence = 0.97f,
            aiSuggestedTags = listOf("Sanitation", "Health Hazard", "Garbage"),
            assignedOfficer = "Dr. Anand Verma"
        ),
        Grievance(
            id = "GRV-1004",
            title = "Severe Pothole Cluster causing Traffic Bottleneck",
            description = "Multiple deep potholes near the flyover junction damaging vehicles and causing traffic jams during peak hours.",
            department = "Public Works",
            priority = GrievancePriority.CRITICAL,
            status = GrievanceStatus.ESCALATED,
            latitude = 12.9510,
            longitude = 77.6120,
            locationName = "Flyover Junction, Ring Road",
            timestamp = System.currentTimeMillis() - 86400000L * 3,
            aiConfidence = 0.99f,
            aiSuggestedTags = listOf("Pothole", "Road Safety", "Escalated SLA"),
            assignedOfficer = "Er. Suresh Patel"
        ),
        Grievance(
            id = "GRV-1005",
            title = "Broken Drainage Cover on Pedestrian Walkway",
            description = "Open concrete drainage slab poses severe injury risk to pedestrians walking near the community park.",
            department = "Municipal Administration",
            priority = GrievancePriority.MEDIUM,
            status = GrievanceStatus.RESOLVED,
            latitude = 12.9780,
            longitude = 77.5890,
            locationName = "Community Park Walkway",
            timestamp = System.currentTimeMillis() - 86400000L * 4,
            aiConfidence = 0.96f,
            aiSuggestedTags = listOf("Drainage", "Hazard", "Resolved"),
            assignedOfficer = "Ms. Sunita Rao"
        )
    )

    private val _grievances = MutableStateFlow(initialGrievances)
    val grievances: StateFlow<List<Grievance>> = _grievances.asStateFlow()

    fun addGrievance(grievance: Grievance) {
        _grievances.update { current -> listOf(grievance) + current }
    }

    fun updateStatus(id: String, newStatus: GrievanceStatus) {
        _grievances.update { current ->
            current.map { if (it.id == id) it.copy(status = newStatus) else it }
        }
    }

    fun getGrievanceById(id: String): Grievance? {
        return _grievances.value.find { it.id == id }
    }
}
