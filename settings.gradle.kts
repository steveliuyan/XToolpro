pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "XToolpro"

include(
    ":app-shell",
    ":core-model",
    ":core-platform",
    ":feature-proxy",
    ":feature-cleaner",
    ":feature-media",
    ":feature-image",
    ":engine-proxy",
    ":engine-cleaner",
    ":engine-media",
    ":engine-image",
)
