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
    namespace = "org.mathieu.domain"
    compileSdk = compileVersion.toInt()

    defaultConfig {
        minSdk = minVersion.toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
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
val koinVersion: String by project
val coreKtxVersion: String by project

dependencies {

    implementation("androidx.core:core-ktx:$coreKtxVersion")

    //Koin
    implementation( "io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
}
// endregion
