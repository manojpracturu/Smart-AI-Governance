package com.example.unifiedai_poweredsmartgrievancemanagementsystem.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Grievance(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val department: String,
    val priority: GrievancePriority,
    val status: GrievanceStatus = GrievanceStatus.SUBMITTED,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    val mediaUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val aiConfidence: Float = 0.94f,
    val aiSuggestedTags: List<String> = emptyList(),
    val assignedOfficer: String = "AI Auto-Assigned Officer",
    val isDuplicate: Boolean = false,
    val duplicateWarning: String? = null
)

@Serializable
enum class GrievancePriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical")
}

@Serializable
enum class GrievanceStatus(val displayName: String) {
    SUBMITTED("Submitted"),
    ASSIGNED("Assigned"),
    AI_CLASSIFIED("AI Classified"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    ESCALATED("Escalated"),
    REJECTED("Rejected")
}

val Departments = listOf(
    "Revenue",
    "Health",
    "Police",
    "Water Supply",
    "Electricity",
    "Municipal Administration",
    "Education",
    "Public Works",
    "Transport",
    "Environment & Sanitation"
)
