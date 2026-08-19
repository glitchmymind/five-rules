rootProject.name = "five-rules"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":common:models")
include(":common:core")
include(":common:network")
include(":common:di")
include(":common:navigation")
include(":common:uikit")

include(":features:auth:api")
include(":features:auth:core")
include(":features:auth:ui")
include(":features:home:api")
include(":features:home:core")
include(":features:home:ui")
include(":features:rules:api")
include(":features:rules:core")
include(":features:rules:ui")
include(":features:feed:api")
include(":features:feed:core")
include(":features:feed:ui")

include(":composeApp")

include(":server:core")
include(":server:di")
include(":server:db")
include(":server:network")
include(":server:user")
include(":server:auth")
include(":server:rules")
include(":server:feed")
include(":server:gateway")
