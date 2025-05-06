
plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.fedatingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fedatingapp"
        minSdk = 26
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
        viewBinding=true
    }
}

dependencies {
    implementation ("androidx.core:core:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.core:core-ktx:1.16.0")
    implementation("androidx.core:core:1.13.0")

    // SliderView library
    implementation("com.github.smarteist:Android-Image-Slider:1.4.0")

    // Shape of View
    implementation("io.github.florent37:shapeofview:1.4.7")

    // Circle Image View
    implementation("de.hdodenhof:circleimageview:3.0.1")

    // Load image
    implementation("com.github.bumptech.glide:glide:4.10.0")

    // Placeholder View
    implementation("com.github.Jehyeok:PlaceHolderView:0.6.2")
    //    implementation("com.mindorks:placeholderview:0.7.3")
    //    implementation("com.android.support:recyclerview-v7:27.1.0")
    //    implementation("com.github.janishar:PlaceHolderView:1.0.3")
    //    annotationProcessor("com.github.janishar:placeholderview-compiler:1.0.3")

    // Gson
    implementation("com.google.code.gson:gson:2.8.6")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Retrofit Gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp3
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}