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
-dontoptimize
-dontpreverify

-keep class com.ganesh.hilt.firebase.livechat.data.** { *; }



# Keep - native method names.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep - Android classes
-keep class android.** { *; }
-keep class androidx.** { *; }

# Keep - Support library classes
-keep class android.support.** { *; }
-keep class androidx.appcompat.** { *; }

# Keep - Google Play Services
-keep class com.google.android.gms.** { *; }

# Keep - Gson
-keep class com.google.gson.** { *; }

# Keep - Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep - OkHttp
-keep class okhttp3.** { *; }

# Keep Glide classes
-keep class com.bumptech.glide.** { *; }
-keep class com.github.bumptech.glide.** { *; }

# Keep generated API
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep class * extends com.bumptech.glide.annotation.GlideModule { *; }


# CoordinatorLayout resolves the behaviors of its child components with reflection.
-keep public class * extends androidx.coordinatorlayout.widget.CoordinatorLayout$Behavior {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

# Make sure we keep annotations for CoordinatorLayout's DefaultBehavior
-keepattributes RuntimeVisible*Annotation*

#************** Facebook ***************
-keep class com.facebook.** { *; }

#************** HTTP loopj ***************
-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }
-keep class org.apache.** { *; }
-keep class org.apache.harmony.xnet.** { *; }
-dontwarn org.apache.commons.**

-keep class org.conscrypt.** { *; }

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn java.beans.**

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep classes that use Firestore serialization
-keepnames class * extends com.google.firebase.firestore.IgnoreExtraProperties

# Keep Firebase Firestore SDK classes
-keep class com.google.firebase.firestore.** { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class com.google.dagger.hilt.** { *; }
-keep class dagger.internal.** { *; }

# Keep Hilt generated components
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltWrapper_** { *; }

# Keep Kotlin coroutine classes
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Firebase Authentication classes
-keep class com.google.firebase.auth.** { *; }

# Keep Firebase Messaging classes
-keep class com.google.firebase.messaging.** { *; }

# Keep Firebase Functions classes
-keep class com.google.firebase.functions.** { *; }

# Keep ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.ViewModelProvider { *; }

# Keep AndroidX Navigation Components
-keep class androidx.navigation.** { *; }

# Keep Volley classes
-keep class com.android.volley.** { *; }

-printusage usage.txt