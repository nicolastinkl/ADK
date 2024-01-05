plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.test.adsdk_demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.test.adsdk_demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Json Parser
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    //okio and okhttp
    implementation(files("libs/okhttp-4.10.0.jar"))
    implementation( files("libs/okio-jvm-3.0.0.jar"))
    implementation( files("libs/protobuf-javalite-3.21.12.jar"))

    // Recommended Google Play Services libraries to support app set ID (v6.10.3 and above)
    implementation ("com.google.android.gms:play-services-tasks:18.0.2")
    implementation ("com.google.android.gms:play-services-appset:16.0.2")

    // Recommended Google Play Services libraries to support Google Advertising ID
    implementation ("com.google.android.gms:play-services-basement:18.2.0")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")

}