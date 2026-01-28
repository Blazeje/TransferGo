plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.transfer.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 27
    }

    android {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        kotlin {
            compilerOptions {
                jvmTarget.set(
                    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
                )
            }
        }
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.moshi)
    implementation(libs.retrofit.moshi)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
