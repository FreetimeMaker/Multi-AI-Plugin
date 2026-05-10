package com.freetime.maip.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Service(Service.Level.PROJECT)
class ChatService(val project: Project) {
    private val _messages = MutableSharedFlow<ChatDelta>()
    val messages = _messages.asSharedFlow()

    suspend fun emitDelta(sender: String, text: String, isNewMessage: Boolean = false) {
        _messages.emit(ChatDelta(sender, text, isNewMessage))
    }
}

data class ChatDelta(val sender: String, val text: String, val isNewMessage: Boolean)
