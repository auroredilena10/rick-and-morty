// region Plugins : dépôts et versions
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    // Les versions viennent de gradle.properties, seul endroit du projet où figure un numéro de version ;
    // les build.gradle.kts appliquent ensuite les plugins sans en répéter aucun.
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
// Gradle 8.0 marque encore cette API comme expérimentale, alors qu'elle vient du modèle de projet d'Android Studio :
// l'avertissement est masqué ici seulement.
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // Les dépôts ne sont déclarés qu'ici : un module qui en ajouterait un ferait échouer le build.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
// endregion

// region Modules
rootProject.name = "Clean RmApi + MVI"

// Point d'entrée : démarre Koin et porte le NavHost.
include(":app")

// Couches partagées : le Domain au centre, Data et UI qui en dépendent.
include(":core:data")
include(":core:domain")
include(":core:ui")

// Une feature par sujet ; aucune ne dépend d'une autre, ni de core:data.
include(":features:characters")
include(":features:locations")
// endregion
