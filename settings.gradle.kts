pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "harmonica"
include("core", "exposed", "gradle-plugin", "integration-test")
includeBuild("demo")
