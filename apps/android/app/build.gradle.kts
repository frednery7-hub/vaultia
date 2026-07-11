import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.vaultia.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vaultia.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        debug {
            isDebuggable = true
        }

        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation("junit:junit:4.13.2")

    // Phase 22 — KDF Dependency Evaluation: dependência adicionada
    // exclusivamente para medição de impacto no APK e benchmark isolado.
    // NÃO integrada a CryptoService nem a qualquer fluxo real do app
    // nesta fase. Ver docs/implementation/ANDROID_FOUNDATION_PHASE_22_PLAN.md.
    implementation("com.lambdapioneer.argon2kt:argon2kt:1.6.0")

    // Phase 22 — testes instrumentados (primeira vez no projeto).
    // Necessárias para resolver androidx.test.ext.junit.runners.AndroidJUnit4
    // usado pelo benchmark Argon2id em androidTest/.
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
