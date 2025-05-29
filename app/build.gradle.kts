plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.kalistanics" // ודא שזה תואם את שם החבילה שלך בקוד
    compileSdk = 34 // הגרסה העדכנית ביותר שבה הפרויקט מתבסס

    defaultConfig {
        applicationId = "com.example.kalistanics" // ודא שזה תואם את שם החבילה שלך
        minSdk = 24 // גרסת אנדרואיד מינימלית נתמכת
        targetSdk = 34 // גרסת אנדרואיד המטרה
        versionCode = 1 // מספר גרסה עבור חנות האפליקציות
        versionName = "1.0" // שם גרסה למעקב

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // עבור בדיקות אוטומטיות
    }

    buildTypes {
        release {
            isMinifyEnabled = false // אם תגדיר true, קובץ ה-APK ידחס
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // תמיכה ב-Java 11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // תלויות לפרויקט
    implementation(libs.appcompat) // AppCompatActivity ותמיכה אחורנית
    implementation(libs.material) // עיצוב חומר (Material Design)
    implementation(libs.activity) // תלות לניהול פעילויות
    implementation(libs.constraintlayout) // פריסות מסוג Constraint
    testImplementation(libs.junit) // עבור בדיקות יחידה
    androidTestImplementation(libs.ext.junit) // בדיקות מורחבות
    androidTestImplementation(libs.espresso.core) // בדיקות UI
}
