package com.freetime.maip.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.freetime.maip.settings.AppSettingsState",
    storages = [Storage("MultiAiPluginSettings.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var openAiKey: String = ""
    var anthropicKey: String = ""
    var geminiKey: String = ""
    var selectedProvider: String = "OpenAI"

    override fun getState(): AppSettingsState = this

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}
