@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("org.jetbrains.dokka") version "2.0.0"
    signing
}

group = "io.github.faridsolgi"
version = "0.0.13-beta1"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    wasmJs{
        browser()
        nodejs()
        d8()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kotlinx.datetime)
                implementation(libs.io.github.faridsolgi.persianDateTime)
                implementation(compose.materialIconsExtended)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "io.github.faridsolgi.persianDatePicker"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
dependencies {
    debugImplementation(compose.uiTooling)
}
compose.resources {
    publicResClass = false
    generateResClass = auto
}
// javadoc jar با dokka
tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
}

tasks.named("publishKotlinMultiplatformPublicationToMavenLocal") {
    dependsOn("signKotlinMultiplatformPublication")
    dependsOn("signJvmPublication")
    dependsOn("signWasmJsPublication")
    dependsOn("signAndroidReleasePublication")

}
signing {
    useInMemoryPgpKeys(
        System.getenv("MAVEN_KEY_ID"),
        System.getenv("MAVEN_SECRET_KEY"),
        System.getenv("MAVEN_GPG_PASSWORD")
    )
    sign(publishing.publications)
}
tasks.withType<PublishToMavenLocal>().configureEach {
    dependsOn(tasks.withType<Sign>())
}
// مطمئن شو هر publication سورس + javadoc داره
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "persian-date-picker", version.toString())


    pom {
        name = "Persian Date Picker Kotlin Multiplatform"
        description = "A Kotlin Multiplatform library providing a Persian (Jalali) Date Picker."
        inceptionYear = "2025"
        url = "https://github.com/faridsolgi/Persian-Date-picker-kotlin-multiplatform"
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("github")
                name.set("Farid Solgi")
                email.set("solgifarid@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.io/github/Persian-Date-picker-kotlin-multiplatform.git")
            developerConnection.set("scm:git:ssh://github.io:github/Persian-Date-picker-kotlin-multiplatform.git")
            url.set("https://github.com/faridsolgi/Persian-Date-picker-kotlin-multiplatform")
        }
    }
}
