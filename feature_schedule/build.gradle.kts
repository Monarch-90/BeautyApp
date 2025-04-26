plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.tatiana.feature_schedule"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /** Модули */
    implementation(project(":data"))

    // ------ AndroidX ------
    /** AppCompat */
    implementation(libs.androidx.appcompat)

    /** Fragment */
    implementation(libs.androidx.fragment.ktx) // Для Fragment KTX

    /** ViewModel */
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel KTX

    /** LiveData */
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData KTX

    /** Material Components */
    implementation(libs.material)

    /** RecyclerView */
    implementation(libs.androidx.recyclerview)

    /** Paging 3 Runtime */
    implementation(libs.androidx.paging.runtime.ktx)

    /** Navigation Component */
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    /** Hilt (только аннотации, основная настройка в :app) */
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}