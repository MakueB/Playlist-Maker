# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Navigation Component
# Navigation Component
-keep class androidx.navigation.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.navigation.* <methods>;
}

# Сохраняем класс Playlist и его конструктор
-keep class com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist { *; }
-keepclassmembers class com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist {
    public <init>(...);
}

# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity

# Retrofit
-keep class com.makiev.playlistmaker.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}

