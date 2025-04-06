plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fedatingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fedatingapp"
        minSdk = 21
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // SliderView library
    implementation("com.github.smarteist:Android-Image-Slider:1.4.0")
    // Shape of View
    implementation("io.github.florent37:shapeofview:1.4.7")
    // Circle Image View
    implementation("de.hdodenhof:circleimageview:3.0.1")
    // Load image
    implementation("com.github.bumptech.glide:glide:4.10.0")
    // Placeholder View
    implementation("com.github.janishar:PlaceHolderView:1.0.3")
    // Gson
    implementation("com.google.code.gson:gson:2.8.6")

}