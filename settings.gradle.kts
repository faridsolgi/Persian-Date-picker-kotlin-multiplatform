pluginManagement {
    repositories {
        maven(uri("https://en-mirror.ir/"))
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(uri("https://en-mirror.ir/"))
        google()
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "persian-date-picker"
include(":library")
