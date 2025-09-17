import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val apiKey: String = (
        localProps.getProperty("API_KEY")
            ?: (project.findProperty("API_KEY") as String?)
            ?: System.getenv("API_KEY")
            ?: ""
        )
if (apiKey.isBlank()) {
    throw GradleException("API_KEY bulunamadı. local.properties / -PAPI_KEY / ENV API_KEY kullan.")
}

val cameraApiKey: String = (
        localProps.getProperty("CAMERA_AI_API")
            ?: (project.findProperty("CAMERA_AI_API") as String?)
            ?: System.getenv("CAMERA_AI_API")
            ?: ""
        )
if (cameraApiKey.isBlank()) {
    throw GradleException("API_KEY bulunamadı. local.properties / -PAPI_KEY / ENV API_KEY kullan.")
}

android {
    namespace = "com.kivinecostone.yemek_tarif_uygulamasi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kivinecostone.yemek_tarif_uygulamasi"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "CAMERA_AI_API", "\"$apiKey\"")
    }


    buildFeatures {
        buildConfig = true
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
    implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("org.json:json:20230227")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")

    implementation("com.prolificinteractive:material-calendarview:1.4.3")
}
