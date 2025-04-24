plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ccafound1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ccafound1"
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
}

dependencies {
    // Firebase BoM to manage all Firebase versions
    implementation(platform(libs.firebase.bom))

    // Firebase libraries (Firebase Firestore, Auth, etc.)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)
    implementation(libs.com.google.firebase.firebase.database2)
    implementation(libs.google.firebase.storage)
    implementation(libs.google.firebase.inappmessaging)

    // Google Sign-In and Play Services
    implementation(libs.play.services.auth.v2000)

    // MPAndroidChart for charting

    implementation(libs.firebase.analytics)
    implementation(libs.image.labeling)

    // Other dependencies (UI, image loading, etc.)
    implementation(libs.squareup.picasso)
    implementation(libs.glide)

    // RecyclerView, Material Design, AppCompat
    implementation(libs.recyclerview)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity.v170)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

