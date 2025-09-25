plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.lab11_elkak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lab11_elkak"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)


    // Google Play Services
    implementation(libs.playServices.maps)
    implementation(libs.playServices.location)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation(libs.gson)

    // Glide
    implementation(libs.glide)
    implementation(libs.firebase.crashlytics.buildtools)
    annotationProcessor(libs.glideCompiler)



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    dependencies {
        implementation ("com.google.mlkit:face-detection:16.1.7")
        implementation ("androidx.camera:camera-camera2:1.4.2")
        implementation ("androidx.camera:camera-lifecycle:1.4.2")
        implementation ("androidx.camera:camera-view:1.4.2-alpha10")
        implementation ("com.google.guava:guava:31.1-android")
    }

}
