import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.kotlin.serialization)
    kotlin("plugin.serialization") version "1.8.0" // Same version as Kotlin
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation("io.ktor:ktor-client-android:2.3.1") // Android Ktor engine

            implementation("io.insert-koin:koin-core:3.4.0")
            implementation("io.insert-koin:koin-android:3.4.0") // For Android
            implementation("io.insert-koin:koin-androidx-compose:3.4.0") // For Jetpack Compose

            // Ktor Core dependency for shared code
            implementation("io.ktor:ktor-client-core:2.3.1")
            
            // Ktor serialization and JSON support
//            implementation("io.ktor:ktor-client-serialization:2.3.1")
//            {
//                exclude(group = "io.ktor", module = "ktor-client-android") // Exclude Android engine
//            }
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")

            // Kotlin Coroutines Core - For general coroutine functionality
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

            // Kotlin Coroutines for Android (allows using coroutines in UI thread)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
            // Ktor Content Negotiation Plugin
            implementation("io.ktor:ktor-client-content-negotiation:2.3.1")

            // Optional Ktor Authentication and Logging Plugins
            implementation("io.ktor:ktor-client-auth:2.3.1")

            implementation("io.ktor:ktor-client-logging:2.3.1")

            // Kotlinx Serialization for JSON parsing
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            implementation("io.ktor:ktor-client-okhttp:2.0.0")

            implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")  // Add this line

            implementation ("androidx.navigation:navigation-compose:2.4.0-alpha01")

            implementation ("androidx.compose.material:material-icons-extended:1.5.0")  // For extended icons

            implementation ("androidx.datastore:datastore-preferences:1.0.0")

            // Room dependencies
            implementation ("androidx.room:room-runtime:2.5.0")
//            kapt ("androidx.room:room-compiler:2.5.0")
            implementation ("androidx.room:room-ktx:2.5.0")
            implementation("com.airbnb.android:lottie-compose:6.1.0")
            implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
            implementation ("org.osmdroid:osmdroid-android:6.1.14")
//            implementation ("com.github.YohannesTz:ChapaKt:1.0.0")
            implementation ("androidx.webkit:webkit:1.8.0")
            implementation ("androidx.room:room-runtime:2.5.0")
//            kapt ("androidx.room:room-compiler:2.5.0")  // For Kotlin
        }
    }
}

android {
    namespace = "com.yome.dildiy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.yome.dildiy"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.foundation.layout.android)  // Coil for image loading
    implementation ("com.google.accompanist:accompanist-pager:0.24.13-rc")
    // Ensure you have the correct version
    implementation ("com.google.android.exoplayer:exoplayer:2.18.0")

    // Android-specific dependencies for Ktor and Koin
    implementation("io.ktor:ktor-client-android:2.3.1") // Android Ktor engine
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.androidx.core)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.play.services.location)
    implementation(libs.androidx.core.i18n)
//    implementation(libs.constraintlayout) // Koin Android support


    debugImplementation(compose.uiTooling)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

