package com.freetime.maic.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Multi AI Chat Settings"
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        return mySettingsComponent!!.openAiKeyText != settings.openAiKey ||
               mySettingsComponent!!.anthropicKeyText != settings.anthropicKey ||
               mySettingsComponent!!.geminiKeyText != settings.geminiKey
    }

    override fun apply() {
        val settings = AppSettingsState.instance
        settings.openAiKey = mySettingsComponent!!.openAiKeyText
        settings.anthropicKey = mySettingsComponent!!.anthropicKeyText
        settings.geminiKey = mySettingsComponent!!.geminiKeyText
    }

    override fun reset() {
        val settings = AppSettingsState.instance
        mySettingsComponent!!.openAiKeyText = settings.openAiKey
        mySettingsComponent!!.anthropicKeyText = settings.anthropicKey
        mySettingsComponent!!.geminiKeyText = settings.geminiKey
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}

class AppSettingsComponent {
    val panel: JPanel
    private val openAiKeyField = JTextField()
    private val anthropicKeyField = JTextField()
    private val geminiKeyField = JTextField()

    var openAiKeyText: String
        get() = openAiKeyField.text
        set(newText) { openAiKeyField.text = newText }

    var anthropicKeyText: String
        get() = anthropicKeyField.text
        set(newText) { anthropicKeyField.text = newText }

    var geminiKeyText: String
        get() = geminiKeyField.text
        set(newText) { geminiKeyField.text = newText }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("OpenAI API Key: "), openAiKeyField, 1, false)
            .addLabeledComponent(JBLabel("Anthropic API Key: "), anthropicKeyField, 1, false)
            .addLabeledComponent(JBLabel("Gemini API Key: "), geminiKeyField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
