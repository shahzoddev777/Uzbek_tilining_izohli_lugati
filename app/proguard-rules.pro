# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Room rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep class shahzod.projects.ozbektiliningizohliugati.database.entity.** { *; }

# Navigation rules
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>(...);
}

# ViewBinding Property Delegate rules (if needed)
-keep class dev.androidbroadcast.vbpd.** { *; }

# WorkManager rules
-keep class shahzod.projects.ozbektiliningizohliugati.notification.WordOfTheDayWorker { *; }

