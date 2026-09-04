plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
android {
    namespace = "com.steveliuyan.xtoolpro.feature.proxy"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
}
dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-platform"))
    implementation(project(":engine-proxy"))
}
