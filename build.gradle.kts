buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
        classpath(libs.secrets.gradle.plugin)
    }
}
plugins {
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
