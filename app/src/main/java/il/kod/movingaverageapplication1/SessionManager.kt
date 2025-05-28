package il.kod.movingaverageapplication1

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class SessionManager @Inject constructor(val preferences: SharedPreferences): Service() {


    fun logOutClient() {
        preferences.edit { clear() }

}

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}