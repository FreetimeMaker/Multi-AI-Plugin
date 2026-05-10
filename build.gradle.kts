plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij.platform") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "com.freetime"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.2.5")
        instrumentationTools()
    }

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.json:json:20231013")
}

intellijPlatform {
    pluginConfiguration {
        id = "com.freetime.maic"
        name = "Multi AI Chat"
        vendor {
            name = "Freetime Maker"
        }
        description = """
            Comprehensive Multi AI assistant for JetBrains IDEs. 
            This tool integrates major AI providers including OpenAI (GPT-4), 
            Anthropic (Claude 3), and Google Gemini into your coding workflow.
            Features include code explanation, real-time chat, and multi-provider switching.
        """.trimIndent()
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
}
