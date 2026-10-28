// region Plugins
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
// endregion

// region Android
val compileVersion: String by project
val minVersion: String by project

android {
    namespace = "org.mathieu.ui"
    compileSdk = compileVersion.toInt()

    defaultConfig {
        minSdk = minVersion.toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

}
// endregion

// region Dépendances
val navVersion: String by project
val koinVersion: String by project
val coreKtxVersion: String by project
val lifecycleVersion: String by project
val activityComposeVersion: String by project
val composeBomVersion: String by project
val coilVersion: String by project
val coroutinesVersion: String by project

dependencies {

    //Projects
    implementation(project(":core:domain"))

    //Android
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    //Compose
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Coil
    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-gif:$coilVersion")


    //Koin
    implementation( "io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    //JetBrains
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

}
// endregion
