plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Read the property from local.properties
val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as String? ?: "MISSING_API_KEY"

android {
    namespace = "com.example.googlemapsdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.googlemapsdemo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        // Inject API key into generated strings.xml
        resValue("string", "google_maps_api_key", mapsApiKey)

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.google.maps)
}