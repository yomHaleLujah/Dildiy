plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.kotlin.serialization) apply false
//    kotlin("plugin.serialization") version "1.8.0" // Use the matching version of the plugin

//    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.kotlin.serialization) apply false
    kotlin("plugin.serialization") version "1.8.0" // Replace with your Kotlin version

}