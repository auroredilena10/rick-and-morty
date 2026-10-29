// region Dépôts des plugins
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
// endregion

// region Dépôts des dépendances
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
