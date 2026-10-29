// region Plugins : dépôts et versions
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    // Les versions des plugins sont lues dans gradle.properties, le seul fichier du projet avec des numéros de version.
    // Les build.gradle.kts appliquent ensuite les plugins sans répéter la version.
    val agpVersion: String by settings
    val kotlinVersion: String by settings
    val realmVersion: String by settings

    plugins {
        id("com.android.application") version agpVersion
        id("com.android.library") version agpVersion
        id("org.jetbrains.kotlin.android") version kotlinVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("io.realm.kotlin") version realmVersion
    }
}
// endregion

// region Dépôts des dépendances
// Gradle 8.0 dit que cette API est encore expérimentale, alors que c'est le modèle de projet d'Android Studio
// qui l'utilise. On masque l'avertissement seulement sur ce bloc.
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // Les dépôts sont déclarés ici et nulle part ailleurs : un module qui en ajoute un fait échouer le build.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
// endregion

// region Modules
rootProject.name = "Clean RmApi + MVI"

// Le point d'entrée : il démarre Koin et contient le NavHost.
include(":app")

// Les couches partagées : le Domain au centre, Data et UI dépendent de lui.
include(":core:data")
include(":core:domain")
include(":core:ui")

// Une feature par sujet. Aucune ne dépend d'une autre feature, ni de core:data.
include(":features:characters")
include(":features:locations")
// endregion
