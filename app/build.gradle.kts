plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ccafound1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ccafound1"
        minSdk = 26
        //noinspection OldTargetApi
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
        compose = true
        buildConfig = true
        mlModelBinding = true
    }

}

dependencies {
    // Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Firebase libraries
    implementation("com.google.firebase:firebase-messaging:25.0.1")
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.database)
    implementation(libs.google.firebase.storage)
    implementation(libs.google.firebase.inappmessaging)
    implementation(libs.firebase.analytics)

    //mlkit
    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation(libs.genai.image.description)

    // Google Sign-In
    implementation(libs.play.services.auth.v2000)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Image Loading
    implementation(libs.squareup.picasso)
    implementation(libs.glide)
    implementation(libs.circleimageview)

    // UI / AndroidX
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.recyclerview)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity.v170)
    implementation(libs.cardview)
    implementation(libs.lifecycle.runtime.ktx)

    // Compose (only if using it)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)

    //tflite
    //noinspection Aligned16KB
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation(libs.tensorflow.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)


    // Testing
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.23.0")
    implementation("com.ncorti:slidetoact:0.11.0")
    implementation(libs.mpandroidchart)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

}
