plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/home/fahim/AndroidStudioProjects/Ratatouille/key.jks")
            storePassword = "64742812"
            keyAlias = "key0"
            keyPassword = "64742812"
        }
        create("release") {
            storeFile = file("/home/fahim/AndroidStudioProjects/Ratatouille/key.jks")
            storePassword = "64742812"
            keyAlias = "key0"
            keyPassword = "64742812"
        }
    }
    namespace = "com.horoftech.ratatouille"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.horoftech.ratatouille"
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
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.sdp.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.google.firebase.auth)
    implementation(libs.play.services.auth.v2120)
    implementation (libs.browser)
    implementation (libs.circleimageview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.picasso)

}