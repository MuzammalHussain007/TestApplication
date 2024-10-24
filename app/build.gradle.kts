plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.inventory.testapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.inventory.testapplication"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //Dimen
    implementation ("com.intuit.ssp:ssp-android:1.0.5")
    implementation ("com.intuit.sdp:sdp-android:1.0.5")


    //View Modal
    val lifecycle_version = "2.8.6"
    val arch_version = "2.2.0"

    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // Live Data
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    //
    implementation ( "com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.airbnb.android:lottie:6.4.0")




}