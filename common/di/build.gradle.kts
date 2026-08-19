import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.fiverules.kmp.dependencies)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm()
    js { browser() }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.common.core)
            implementation(projects.common.network)
            implementation(projects.common.navigation)
            implementation(projects.features.auth.core)
            implementation(projects.features.auth.ui)
            implementation(projects.features.home.core)
            implementation(projects.features.home.ui)
            implementation(projects.features.rules.core)
            implementation(projects.features.rules.ui)
            implementation(projects.features.feed.core)
            implementation(projects.features.feed.ui)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noArg)
        }
    }
}

android {
    namespace = "com.fiverules.common.di"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
