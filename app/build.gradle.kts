// region Plugins
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
// endregion

// region Android
val compileVersion: String by project
val minVersion: String by project

android {
    namespace = "org.mathieu.projet2"
    compileSdk = compileVersion.toInt()

    defaultConfig {
        applicationId = "org.mathieu.projet2"
        minSdk = minVersion.toInt()
        targetSdk = compileVersion.toInt()
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Compose part
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    // JVM Part
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        // Les versions récentes proposées (Koin 4, Navigation 2.10) exigent Kotlin 2 : elles casseraient ce projet en Kotlin 1.8.
        disable += "GradleDependency"
    }

}
// endregion

// region Dépendances
val navVersion: String by project
val koinVersion: String by project
val coroutinesVersion: String by project
val composeBomVersion: String by project

dependencies {

    //Projects
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(project(":features:characters"))

    //Compose
    // La BOM fixe la version de material3 pour toutes les configurations, y compris celle analysée par le Lint.
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //Koin
    implementation("io.insert-koin:koin-android:$koinVersion")

}
// endregion
