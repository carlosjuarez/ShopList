plugins {
    id("com.juvcarl.shoplist.application")
    id("com.juvcarl.shoplist.application.compose")
    id("com.juvcarl.shoplist.application.jacoco")
    kotlin("kapt")
    id("jacoco")
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    alias(libs.plugins.firebase.crashlytics)
}

android {
    defaultConfig {
        applicationId = "com.juvcarl.shoplist"
        versionCode = 1
        versionName = "0.0.1" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "com.juvcarl.shoplist.testing.ShopListTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    signingConfigs {
        create("release") {
            keyAlias = "shoplist"
            keyPassword = "shoplist"
            storeFile = file("./keystore/shoplist.jks")
            storePassword = "shoplist"
        }
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
    }

    // @see Flavor for more details on the app product flavors.
    /*flavorDimensions += FlavorDimension.contentType.name
    productFlavors {
        Flavor.values().forEach {
            create(it.name) {
                dimension = it.dimension.name
                if (it.applicationIdSuffix != null) {
                    applicationIdSuffix = it.applicationIdSuffix
                }
            }
        }
    }*/

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.juvcarl.shoplist"

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

dependencies {

    androidTestImplementation(libs.androidx.navigation.testing)
    debugImplementation(libs.androidx.compose.ui.testManifest)
    androidTestImplementation(libs.room.testing)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)


    implementation(libs.play.services.nearby)

    api(libs.junit4)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)

    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.ui.test)
    api(libs.hilt.android.testing)
    api(libs.androidx.hilt.navigation.compose)

    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
    configurations.configureEach {
        resolutionStrategy {
            force(libs.junit4)
            // Temporary workaround for https://issuetracker.google.com/174733673
            force("org.objenesis:objenesis:2.6")
        }
    }
}
