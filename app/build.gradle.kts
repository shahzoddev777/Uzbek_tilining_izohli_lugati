plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
}

android {
    namespace = "shahzod.projects.ozbektiliningizohliugati"
    compileSdk = 37

    defaultConfig {
        applicationId = "shahzod.projects.ozbektiliningizohliugati"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    //room
    val room_version = "2.8.4"
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("net.datafaker:datafaker:2.5.4")

    //viewbinding
    implementation("dev.androidbroadcast.vbpd:vbpd:2.0.4")
    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")
    //Material components
    implementation("com.google.android.material:material:1.9.0")
    //notification
    implementation("androidx.work:work-runtime-ktx:2.9.1")
}