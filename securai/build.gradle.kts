plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.nomaddeveloper.securai"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["release"])
            groupId = "com.github.yunusemrehalil"
            artifactId = "securai"
            version = "1.0.0-beta"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    //MediaPipe
    implementation(libs.tasks.text)
    //OkHttp v Retrofit
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    //Gson
    implementation(libs.gson)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}