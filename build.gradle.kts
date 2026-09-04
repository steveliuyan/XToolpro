import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        ktlint()
    }
    format("markdown") {
        target("**/*.md")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

val allowedProjectDependencies =
    mapOf(
        ":app-shell" to setOf(":core-model", ":core-platform", ":feature-proxy", ":feature-cleaner", ":feature-media", ":feature-image"),
        ":core-model" to emptySet(),
        ":core-platform" to setOf(":core-model"),
        ":feature-proxy" to setOf(":core-model", ":core-platform", ":engine-proxy"),
        ":feature-cleaner" to setOf(":core-model", ":core-platform", ":engine-cleaner"),
        ":feature-media" to setOf(":core-model", ":core-platform", ":engine-media"),
        ":feature-image" to setOf(":core-model", ":core-platform", ":engine-image"),
        ":engine-proxy" to setOf(":core-model"),
        ":engine-cleaner" to setOf(":core-model"),
        ":engine-media" to setOf(":core-model"),
        ":engine-image" to setOf(":core-model"),
    )

tasks.register("verifyModuleBoundaries") {
    group = "verification"
    description = "Fails when a module introduces an unapproved project dependency."

    doLast {
        val featureEnginePairs =
            mapOf(
                ":feature-proxy" to ":engine-proxy",
                ":feature-cleaner" to ":engine-cleaner",
                ":feature-media" to ":engine-media",
                ":feature-image" to ":engine-image",
            )
        val forbiddenFeatureDependencies =
            featureEnginePairs.keys.flatMap { featurePath ->
                allowedProjectDependencies.getValue(featurePath)
                    .filter { dependencyPath -> dependencyPath.startsWith(":feature-") }
                    .map { dependencyPath -> "$featurePath -> $dependencyPath" }
            }

        check(forbiddenFeatureDependencies.isEmpty()) {
            "Feature modules must not depend on other feature modules: ${forbiddenFeatureDependencies.joinToString()}"
        }
        check(
            featureEnginePairs.all { (featurePath, enginePath) ->
                enginePath in allowedProjectDependencies.getValue(featurePath)
            },
        ) {
            "Every feature module must allow only its matching engine boundary."
        }

        val violations =
            allowedProjectDependencies.flatMap { (projectPath, allowedDependencies) ->
                val project = project(projectPath)
                val configuredDependencies =
                    listOf("api", "implementation")
                        .mapNotNull(project.configurations::findByName)
                        .flatMap { configuration ->
                            configuration.dependencies.withType(ProjectDependency::class.java)
                                .map { it.dependencyProject.path }
                        }
                        .distinct()

                configuredDependencies
                    .filterNot(allowedDependencies::contains)
                    .map { dependencyPath -> "$projectPath -> $dependencyPath is not allowed" }
            }

        check(violations.isEmpty()) {
            "Module boundary violations:\n${violations.joinToString("\n")}"
        }
    }
}

tasks.register("verifyProject") {
    group = "verification"
    description = "Runs the baseline module-boundary and test checks."
    dependsOn("verifyModuleBoundaries", ":core-model:test", "spotlessCheck")
}
