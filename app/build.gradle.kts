plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}
android {
    namespace = "com.example.mobileapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileapp"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

}

dependencies {

    implementation(libs.firebase.auth)
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("com.applandeo:material-calendar-view:1.9.2")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation ("com.google.firebase:firebase-auth:23.1.0")
    implementation ("com.google.firebase:firebase-firestore:24.6.0")
    implementation("com.google.firebase:firebase-database")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}
