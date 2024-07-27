import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.gluonhq.gluonfx-gradle-plugin") version "1.0.23"
}

group = "com.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

application {
    mainClass.set("com.project.panel.AppRunKt")
    applicationDefaultJvmArgs = listOf("-Xmx500m")
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
}

kotlin {
    jvmToolchain(22)
}

javafx {
    version = "20"
    modules = listOf("javafx.controls", "javafx.fxml")
}

gluonfx {
    bundlesList = listOf("com.project.panel")
    reflectionList = listOf(
        "com.project.panel.ui.controller.BaseController",
        "com.project.panel.ui.controller.MainController",

        "com.project.panel.data.SteamData",
        "com.project.panel.data.Session",

        "com.project.panel.retrofit.api.ProfileApi",
        "com.project.panel.retrofit.response.SteamProfileResponse",
    )
}

dependencies {
    implementation("com.sikulix:sikulixapi:2.0.5")

    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "22"
    }
}
