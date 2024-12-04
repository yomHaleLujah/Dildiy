//package com.yome.dildiy.networking
//
//import com.yome.dildiy.Platform
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.HttpClientEngineFactory
//import io.ktor.client.engine.cio.CIO // Default engine (works on Android, iOS, etc.)
//import io.ktor.client.engine.android.Android
//import io.ktor.client.features.json.JsonFeature
//import io.ktor.client.plugins.json.Json
//import io.ktor.client.plugins.json.serializer.KotlinxSerializer
//import org.jetbrains.skiko.OS
//import org.koin.dsl.module
//
//val networkModule = module {
//
//    // Provide HttpClient with platform-specific engine
//    single {
//        HttpClient(get()) {
//            install(JsonFeature) {
//                serializer = KotlinxSerializer()  // Add the necessary JSON serializer
//            }
//        }
//    }
//
//    // Provide DildiyClient with HttpClient injected
//    single { DildiyClient(get()) }
//}
//
//// Platform-specific engine declaration
//fun getEngine(): HttpClientEngineFactory<*> {
//    return when (Platform.osFamily) {
//        OsFamily.ANDROID -> OS.Android // Use Android engine on Android platform
//        else -> CIO // Default to CIO engine for other platforms
//    }
//}
