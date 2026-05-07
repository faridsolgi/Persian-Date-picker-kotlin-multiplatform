pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.myket.ir")
        }
        maven(uri("https://en-mirror.ir/"))
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.myket.ir")
        }

        maven(uri("https://en-mirror.ir/"))
        google()
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "persian-date-picker"
include(":library")
include(":samples")
