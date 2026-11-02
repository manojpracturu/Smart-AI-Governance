package com.example.unifiedai_poweredsmartgrievancemanagementsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessageItem(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUser: Boolean,
    val attachmentUri: String? = null,
    val attachmentNote: String? = null
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow(
        listOf(
            ChatMessageItem(
                senderName = "Er. Rajesh Kumar",
                messageText = "Hello citizen, I am assigned to your grievance (GRV-1001: Major Water Pipe Burst). Our field unit is dispatched.",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessageItem>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(text: String, attachmentUri: String? = null, attachmentNote: String? = null) {
        if (text.isBlank() && attachmentUri == null && attachmentNote == null) return

        val userMessage = ChatMessageItem(
            senderName = "You (Citizen)",
            messageText = text.ifBlank { "Attached document/photo" },
            isUser = true,
            attachmentUri = attachmentUri,
            attachmentNote = attachmentNote
        )

        _messages.update { it + userMessage }

        // Simulate officer typing indicator and response
        viewModelScope.launch {
            delay(800)
            _isTyping.value = true
            delay(1500)
            _isTyping.value = false

            val officerReply = when {
                text.contains("photo", ignoreCase = true) || attachmentUri != null ->
                    "Thank you for sharing the photo and clarification note. I've updated the inspection record for GRV-1001."
                text.contains("when", ignoreCase = true) || text.contains("time", ignoreCase = true) ->
                    "We expect restoration work to complete within 4 hours. SLA timer is active."
                else ->
                    "Noted. I am personally monitoring the repair progress on site. Feel free to attach any additional clarification."
            }

            val replyMessage = ChatMessageItem(
                senderName = "Er. Rajesh Kumar",
                messageText = officerReply,
                isUser = false
            )
            _messages.update { it + replyMessage }
        }
    }
}
