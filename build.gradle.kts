// Top-level build file where you can add configuration options common to all sub-modules.
plugins {
    // Bumped plugin versions for Gradle 9 compatibility
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.13" apply false
    // Hilt for dependency injection
    id("com.google.dagger.hilt.android") version "2.52" apply false
}
