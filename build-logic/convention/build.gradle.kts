plugins {
    `kotlin-dsl`
}

group = "com.fiverules.gradle"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpDependencies") {
            id = "com.fiverules.kmp.dependencies"
            implementationClass = "com.fiverules.gradle.KmpDependenciesPlugin"
        }
    }
}
