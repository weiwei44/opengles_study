object Versions{
    val support_lib = "27.1.1"
    val compileSdkVersion = 27
    val minSdkVersion = 15
    val targetSdkVersion = 27
    val versionCode = 1
    val versionName = "1.0"
}

object Libs {
    val support_annotations = "com.android.support:support-annotations:${Versions.support_lib}"
    val support_appcompat_v7 = "com.android.support:appcompat-v7:${Versions.support_lib}"
    val constraint = "com.android.support.constraint:constraint-layout:1.1.0"

    val rxjava = "io.reactivex.rxjava2:rxjava:2.0.5"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:2.0.1"
    val rxpermissions = "com.tbruyelle.rxpermissions2:rxpermissions:0.9.3@aar"
}