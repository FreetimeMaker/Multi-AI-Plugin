package com.freetime.maip.actions

import com.freetime.maip.api.AiClientFactory
import com.freetime.maip.services.ChatService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExplainCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText ?: return
        val chatService = project.service<ChatService>()

        if (selectedText.isNotBlank()) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("MultiAIWindow")
            toolWindow?.show()

            CoroutineScope(Dispatchers.IO).launch {
                chatService.emitDelta("System", "Erkläre Code...", true)
                val prompt = "Erkläre mir diesen Code ausführlich:\n\n```\n$selectedText\n```"
                
                chatService.emitDelta("KI", "", true)
                AiClientFactory.getClient().generateResponseStream(prompt).collect { chunk ->
                    chatService.emitDelta("KI", chunk, false)
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor?.selectionModel?.hasSelection() ?: false
    }
}
