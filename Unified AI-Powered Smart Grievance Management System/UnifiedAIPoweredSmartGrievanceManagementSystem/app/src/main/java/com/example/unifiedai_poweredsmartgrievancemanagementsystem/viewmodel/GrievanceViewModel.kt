package com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.ai.AiGrievanceService
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.Grievance
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievancePriority
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceRepository
import com.example.unifiedai_poweredsmartgrievancemanagementsystem.data.GrievanceStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GrievanceViewModel : ViewModel() {
    private val repository = GrievanceRepository()
    private val aiService = AiGrievanceService()

    val grievances: StateFlow<List<Grievance>> = repository.grievances
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _lastSubmittedGrievance = MutableStateFlow<Grievance?>(null)
    val lastSubmittedGrievance: StateFlow<Grievance?> = _lastSubmittedGrievance.asStateFlow()

    fun submitGrievance(
        title: String,
        description: String,
        selectedDepartment: String,
        latitude: Double?,
        longitude: Double?,
        locationName: String?,
        mediaUri: String?,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            // Simulate AI Classification delay & analysis
            delay(1000)

            val analysis = aiService.analyzeGrievance(
                title = title,
                description = description,
                selectedDepartment = selectedDepartment,
                latitude = latitude,
                longitude = longitude,
                existingGrievances = repository.grievances.value
            )

            val newGrievance = Grievance(
                title = title,
                description = description,
                department = analysis.department,
                priority = analysis.priority,
                status = GrievanceStatus.AI_CLASSIFIED,
                latitude = latitude ?: 12.9716,
                longitude = longitude ?: 77.5946,
                locationName = locationName ?: "Current Location (GPS Tagged)",
                mediaUri = mediaUri,
                aiConfidence = analysis.confidence,
                aiSuggestedTags = analysis.tags,
                assignedOfficer = analysis.assignedOfficer,
                isDuplicate = analysis.isDuplicate,
                duplicateWarning = analysis.duplicateWarning
            )

            repository.addGrievance(newGrievance)
            _lastSubmittedGrievance.value = newGrievance
            _isSubmitting.value = false
            onSuccess(newGrievance.id)
        }
    }

    fun updateGrievanceStatus(id: String, status: GrievanceStatus) {
        repository.updateStatus(id, status)
    }

    fun getGrievance(id: String): Grievance? {
        return repository.getGrievanceById(id)
    }
}
