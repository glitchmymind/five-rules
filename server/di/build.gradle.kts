plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.server.core)
    implementation(projects.server.db)
    implementation(projects.server.network)
    implementation(projects.server.user)
    implementation(projects.server.auth)
    implementation(projects.server.rules)
    implementation(projects.server.feed)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
}
