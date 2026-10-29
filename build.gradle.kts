// Top-level build file where you can add configuration options common to all sub-projects/modules.
// region Plugins
// Pas de version ici : elles sont dans gradle.properties, lues par le pluginManagement de settings.gradle.kts.
plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("io.realm.kotlin") apply false
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.android.library") apply false
}
// endregion
