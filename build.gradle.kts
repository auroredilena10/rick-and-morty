// Top-level build file where you can add configuration options common to all sub-projects/modules.
// region Plugins
// Versions déclarées dans le pluginManagement de settings.gradle.kts, à partir de gradle.properties.
plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("io.realm.kotlin") apply false
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.android.library") apply false
}
// endregion
