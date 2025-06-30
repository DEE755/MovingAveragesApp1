package il.kod.movingaverageapplication1.utils

import android.app.Activity
import android.content.pm.ActivityInfo

fun Activity.lockOrientation() {
    requestedOrientation = when (resources.configuration.orientation) {
        android.content.res.Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        android.content.res.Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

fun Activity.unlockOrientation() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}