plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ktor)
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.fiverules.server.gateway.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(projects.server.core)
    implementation(projects.server.di)
    implementation(projects.server.db)
    implementation(projects.server.network)
    implementation(projects.server.user)
    implementation(projects.server.auth)
    implementation(projects.server.rules)
    implementation(projects.server.feed)
    implementation(projects.common.models)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.ktor)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
}
