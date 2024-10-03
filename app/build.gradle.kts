import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.ktlint)
    id("kotlin-kapt")
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

val localProperties = Properties().apply {
    val file = rootProject.file("${rootProject.projectDir.path}/local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

fun getLocalPropertyOrEnv(key: String, default: String = ""): String {
    return localProperties[key]?.toString() ?: System.getenv(key) ?: default
}

android {
    namespace = "com.example.base_clean_architecture"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.base_clean_architecture"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(getLocalPropertyOrEnv(key = "KEYSTORE_FILE_PATH", default = "/"))
            storePassword = getLocalPropertyOrEnv(key = "KEYSTORE_PASSWORD")
            keyAlias = getLocalPropertyOrEnv(key = "KEYSTORE_KEY_ALIAS")
            keyPassword = getLocalPropertyOrEnv(key = "KEYSTORE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {
            versionNameSuffix = "-DEBUG"
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.sdp.android)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.compiler)

    //DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //call api
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}
