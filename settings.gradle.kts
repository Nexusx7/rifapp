pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // 🔹 Repositorios adicionales directos de Google (por si falla el espejo)
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }
        maven { url = uri("https://maven.google.com") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        // 🔹 Extra (refuerza acceso a Firebase)
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "rifapp"
include(":app")
