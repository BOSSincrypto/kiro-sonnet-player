# Add project specific ProGuard rules here.

# Optimization flags
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-dontpreverify

# ExoPlayer
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ExoPlayer - Session
-keep class androidx.media3.session.** { *; }
-keep interface androidx.media3.session.** { *; }

# ExoPlayer - Common
-keep class androidx.media3.common.** { *; }
-dontwarn androidx.media3.common.**

# ExoPlayer - Decoder
-keep class androidx.media3.decoder.** { *; }
-dontwarn androidx.media3.decoder.**

# ExoPlayer - Extractor
-keep class androidx.media3.extractor.** { *; }
-dontwarn androidx.media3.extractor.**

# Hilt
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-dontwarn androidx.compose.**

# DataStore
-keep class androidx.datastore.*.** { *; }

# Keep data classes
-keepclassmembers class com.kiro.sonnetplayer.domain.model.** {
    *;
}

# AndroidX
-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
