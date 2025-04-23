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
    implementation(libs.credentials.vlatesversion)
    implementation(libs.googleid)
    implementation(libs.com.google.firebase.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.glide)
    implementation(libs.play.services.auth.v2100)
    implementation(libs.activity.ktx)
    implementation(libs.glide.v4132)
    implementation(libs.appcompat)
    implementation(libs.volley)
    implementation(libs.image.labeling)
    annotationProcessor(libs.compiler)
    implementation(libs.glide)
    implementation(libs.work.runtime)
    implementation(libs.activity.v170)
    implementation(libs.google.googleid.vlatestversion)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.database)
    implementation(libs.material)
    implementation(libs.play.services.auth.v2130)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}