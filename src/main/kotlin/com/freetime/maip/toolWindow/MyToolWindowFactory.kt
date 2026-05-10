package com.freetime.maip.toolWindow

import com.freetime.maip.api.AiClientFactory
import com.freetime.maip.services.ChatService
import com.freetime.maip.settings.AppSettingsState
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.ContentFactory
import com.intellij.openapi.ui.ComboBox
import kotlinx.coroutines.*
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindowContent(project)
        val content = ContentFactory.getInstance().createContent(myToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private class MyToolWindowContent(val project: Project) {
        val contentPanel = JPanel(BorderLayout())
        private val chatArea = JBTextArea()
        private val inputField = JTextField()
        private val sendButton = JButton("Senden")
        private val providerBox = ComboBox(arrayOf("OpenAI", "Gemini", "Anthropic"))
        private val clearButton = JButton("Leeren")
        private val chatService = project.service<ChatService>()
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        init {
            val topPanel = JPanel(BorderLayout())
            providerBox.selectedItem = AppSettingsState.instance.selectedProvider
            providerBox.addActionListener { AppSettingsState.instance.selectedProvider = providerBox.selectedItem as String }
            topPanel.add(providerBox, BorderLayout.CENTER)
            topPanel.add(clearButton, BorderLayout.EAST)
            
            chatArea.isEditable = false
            chatArea.lineWrap = true
            chatArea.wrapStyleWord = true
            
            val inputPanel = JPanel(BorderLayout())
            inputPanel.add(inputField, BorderLayout.CENTER)
            inputPanel.add(sendButton, BorderLayout.EAST)

            contentPanel.add(topPanel, BorderLayout.NORTH)
            contentPanel.add(JBScrollPane(chatArea), BorderLayout.CENTER)
            contentPanel.add(inputPanel, BorderLayout.SOUTH)

            sendButton.addActionListener { handleUserInput() }
            inputField.addActionListener { handleUserInput() }
            clearButton.addActionListener { chatArea.text = "" }

            scope.launch {
                chatService.messages.collect { delta ->
                    if (delta.isNewMessage) {
                        chatArea.append("\n${delta.sender}: ${delta.text}")
                    } else {
                        chatArea.append(delta.text)
                    }
                    chatArea.caretPosition = chatArea.document.length
                }
            }
        }

        private fun handleUserInput() {
            val text = inputField.text
            if (text.isNotBlank()) {
                inputField.text = ""
                scope.launch {
                    chatService.emitDelta("Du", text, true)
                    chatService.emitDelta("KI", "", true)
                    
                    withContext(Dispatchers.IO) {
                        AiClientFactory.getClient().generateResponseStream(text).collect { chunk ->
                            chatService.emitDelta("KI", chunk, false)
                        }
                    }
                }
            }
        }
    }
}
